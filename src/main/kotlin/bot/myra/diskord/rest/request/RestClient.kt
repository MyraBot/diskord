package bot.myra.diskord.rest.request

import bot.myra.kommons.debug
import bot.myra.kommons.kDebug
import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.Route
import bot.myra.diskord.rest.request.error.validateResponse
import bot.myra.diskord.common.utilities.FileFormats
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.builtins.serializer
import java.util.concurrent.ForkJoinPool

/**
 * Http client for executing rest requests.
 */
object RestClient {
    val coroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())
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
    private suspend fun <R> executeRequest(data: HttpRequest<R>): R? {
        var route = Endpoints.baseUrl + data.route.path
        data.arguments.entries.forEach { route = route.replace("{${it.key}}", it.value.toString()) }

        debug(this::class) { "Rest >>> ${data.route.httpMethod}: $route - ${data.json}" }
        val response = if (data.attachments.isEmpty()) bodyRequest(route, data.route.httpMethod, data.json, data.logReason) // Request doesn't contain files
        else formDataRequest(route, data.json!!, data.attachments) // Request needs to send files
        kDebug(this::class) { "Rest <<< ${response.readText()}" }

        val errorValidation = validateResponse(response)
        if (errorValidation.exception != null) throw errorValidation.exception
        if (errorValidation.returnNull) return null

        @Suppress("UNCHECKED_CAST")
        return if (data.route.serializer == Unit.serializer()) Unit as R
        else {
            val deserialized: R = JSON.decodeFromString(data.route.serializer, response.readText())
            data.route.cache?.invoke(deserialized, data.arguments)
            deserialized
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