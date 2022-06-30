package bot.myra.diskord.rest.request

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.utilities.FileFormats
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.common.utilities.string
import bot.myra.diskord.rest.Route
import bot.myra.diskord.rest.request.error.*
import bot.myra.diskord.rest.request.error.rateLimits.RateLimitWorker
import bot.myra.kommons.debug
import bot.myra.kommons.kDebug
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Http client for executing rest requests.
 */
object RestClient {

    private val httpClient: HttpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            connectTimeoutMillis = 10000
            requestTimeoutMillis = 10000
        }
        expectSuccess = false // Disables throwing exceptions
        defaultRequest {
            url { protocol = URLProtocol.HTTPS }
            header("Authorization", "Bot ${Diskord.token}")
        }
    }

    /**
     * Executes a http request using the [StacktraceRecovery].
     * This is the recommended way to execute requests and is
     * preferred over [executeRequest].
     *
     * @param R The response type.
     * @param route The route to execute.
     * @param request Detailed information about the request.
     * @return Returns the response as [R].
     */
    suspend fun <R> execute(route: Route<R>, request: suspend HttpRequest<R>.() -> Unit = {}): R = StacktraceRecovery.handle(HttpRequest(route).apply { request.invoke(this) })

    /**
     * Executes a nullable http request using the [StacktraceRecovery].
     * This is the recommended way to execute requests and is
     * preferred over [executeRequest].
     *
     * @param R The response type.
     * @param route The route to execute.
     * @param request Detailed information about the request.
     * @return Returns the response as a nullable [R].
     */
    suspend fun <R> executeNullable(route: Route<R>, request: HttpRequest<R>.() -> Unit = {}): R? = StacktraceRecovery.handleNullable(HttpRequest(route).apply { request.invoke(this) })

    /**
     * Executes a http request to the saved path of [req].
     *
     * @param req information about the to be executed [HttpRequest].
     */
    suspend fun <R> executeRequest(req: HttpRequest<R>): R? {
        debug(this::class) { "Rest >>> ${req.route.httpMethod}: ${req.getFullPath()} - ${req.json}" }
        val response = if (req.attachments.isEmpty()) bodyRequest(req) // Request doesn't contain files
        else formDataRequest(req.getFullPath(), req.json!!, req.attachments) // Request needs to send files
        kDebug(this::class) { "Rest <<< ${response.bodyAsText()}" }

        if (response.status.isSuccess()) {
            @Suppress("UNCHECKED_CAST")
            return if (req.route.serializer == Unit.serializer()) Unit as R
            else {
                val deserialized: R = JSON.decodeFromString(req.route.serializer, response.bodyAsText())
                req.route.cache?.invoke(deserialized, req.arguments)
                deserialized
            }
        } else {
            val error = JSON.decodeFromString<JsonObject>(response.bodyAsText())
            val message = error["message"]?.string ?: "No error message provided"
            return when (response.status) {
                HttpStatusCode.NotModified      -> throw ModificationException(message)
                HttpStatusCode.BadRequest       -> throw BadReqException(message)
                HttpStatusCode.Unauthorized     -> throw Exception("Unauthorized") // Internal exception
                HttpStatusCode.Forbidden        -> throw MissingPermissionsException(message)
                HttpStatusCode.NotFound         -> throw NotFoundException(message)
                HttpStatusCode.MethodNotAllowed -> throw Exception("Method not allowed") // Internal exception
                HttpStatusCode.TooManyRequests  -> RateLimitWorker.queue(req, JSON.decodeFromJsonElement(error))
                else                            -> return executeRequest(req)
            }
        }
    }

    /**
     * Executes a http request with an optional body.
     *
     * @param req Holding all information about the request.
     * @return Returns the response as a [HttpResponse].
     */
    private suspend fun bodyRequest(req: HttpRequest<*>): HttpResponse {
        try {
            return httpClient.request(req.getFullPath()) {
                method = req.route.httpMethod
                req.queryParameter.forEach { parameter(it.first, it.second) }
                req.logReason?.let { headers { header("X-Audit-Log-Reason", it) } }
                req.json?.let {
                    contentType(ContentType.Application.Json)
                    setBody(it)
                }
            }
        } catch (e: HttpRequestTimeoutException) {
            debug(RestClient::class) { "Retrying" }
            return bodyRequest(req)
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
            FileFormats.PNG  -> ContentType.Image.PNG
            FileFormats.GIF  -> ContentType.Image.GIF
            FileFormats.TXT  -> ContentType.Text.Plain
            else             -> throw Exception("The provided file type isn't registered as a content type")
        }
    }

}