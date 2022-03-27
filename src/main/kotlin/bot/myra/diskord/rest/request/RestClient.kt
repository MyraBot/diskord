package bot.myra.diskord.rest.request

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.utilities.FileFormats
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.common.utilities.string
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.Route
import bot.myra.diskord.rest.request.error.BadReqException
import bot.myra.diskord.rest.request.error.EntityModifyException
import bot.myra.diskord.rest.request.error.rateLimits.RateLimitWorker
import bot.myra.kommons.debug
import bot.myra.kommons.kDebug
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Http client for executing rest requests.
 */
object RestClient {
    //val coroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val httpClient: HttpClient = HttpClient(CIO) {
        install(HttpTimeout) {
            connectTimeoutMillis = 5000
            requestTimeoutMillis = 5000
        }
        expectSuccess = false // Disables throwing exceptions
        defaultRequest { header("Authorization", "Bot ${Diskord.token}") }
    }

    private suspend fun <R> execute(route: Route<R>, request: HttpRequest<R>.() -> Unit): R? {
        val requestOptions = HttpRequest(route).apply(request)
        return executeRequest(requestOptions)
    }

    fun <R> executeAsync(route: Route<R>, request: HttpRequest<R>.() -> Unit = {}): Deferred<R> {
        val future = CompletableDeferred<R>()
        coroutineScope.launch { future.complete(execute(route, request)!!) }
        return future
    }

    fun <R> executeNullableAsync(route: Route<R>, request: HttpRequest<R>.() -> Unit = {}): Deferred<R?> {
        val future = CompletableDeferred<R?>()
        coroutineScope.launch { future.complete(execute(route, request)) }
        return future
    }


    /**
     * Executes a http request to the saved path of [data].
     *
     * @param data information about the to be executed [HttpRequest].
     */
    internal suspend fun <R> executeRequest(data: HttpRequest<R>): R? {
        var route = Endpoints.baseUrl + data.route.path
        data.arguments.entries.forEach { route = route.replace("{${it.key}}", it.value.toString()) }

        debug(this::class) { "Rest >>> ${data.route.httpMethod}: $route - ${data.json}" }
        val response = if (data.attachments.isEmpty()) bodyRequest(route, data.route.httpMethod, data.json, data.logReason) // Request doesn't contain files
        else formDataRequest(route, data.json!!, data.attachments) // Request needs to send files
        kDebug(this::class) { "Rest <<< ${response.readText()}" }

        if (response.status.isSuccess()) {
            @Suppress("UNCHECKED_CAST")
            return if (data.route.serializer == Unit.serializer()) Unit as R
            else {
                val deserialized: R = JSON.decodeFromString(data.route.serializer, response.readText())
                data.route.cache?.invoke(deserialized, data.arguments)
                deserialized
            }
        } else {
            val error = JSON.decodeFromString<JsonObject>(response.readText())
            val errorMessage = error["message"]?.string ?: "No error message provided"
            when (response.status) {
                HttpStatusCode.NotModified -> throw EntityModifyException(errorMessage)
                HttpStatusCode.BadRequest -> throw BadReqException(errorMessage)
                HttpStatusCode.Unauthorized -> throw Exception() // Internal exception
                HttpStatusCode.Forbidden -> return null
                HttpStatusCode.NotFound -> return null
                HttpStatusCode.MethodNotAllowed -> throw Exception() // Internal exception
                HttpStatusCode.TooManyRequests -> return RateLimitWorker.queue(data,JSON.decodeFromJsonElement(error))
                else -> throw UnknownError()
            }
        }
    }

    /**
     * Executes a normal http request. The http method is determined by [httpMethod].
     *
     * @param route The route to execute.
     * @param json Optional json body.
     * @return Returns the response as a [HttpResponse].
     */
    private suspend fun bodyRequest(route: String, httpMethod: HttpMethod, json: String?, reason: String?): HttpResponse =
        httpClient.request(route) {
            method = httpMethod
            reason?.let { headers { header("X-Audit-Log-Reason", it) } }
            json?.let {
                contentType(ContentType.Application.Json)
                body = it
            }
        }

    /**
     * Used to send files with messages. See [Discords documentation](https://discord.com/developers/docs/reference#uploading-files)
     * or the [documentation of ktor to upload a file](https://ktor.io/docs/request.html#upload_file).
     *
     * @param route The route to execute the request on.
     * @param json Optional json body.
     * @param files File to u upload.
     * @return Returns the response as a [HttpResponse].
     */
    private suspend fun formDataRequest(route: String, json: String, files: List<File>): HttpResponse =
        httpClient.submitFormWithBinaryData(
            url = route,
            formData = formData {
                append("payload_json", json)
                files.forEachIndexed { n, file ->
                    append("files[$n]", file.bytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}.${file.type.extension}\"")
                        append(HttpHeaders.ContentType, getContentType(file).contentType)
                    })
                }
            }
        )

    private fun getContentType(file: File): ContentType {
        return when (file.type) {
            FileFormats.JPEG -> ContentType.Image.JPEG
            FileFormats.PNG -> ContentType.Image.PNG
            FileFormats.GIF -> ContentType.Image.GIF
            else -> throw Exception("The provided file type isn't registered as a content type")
        }
    }

}