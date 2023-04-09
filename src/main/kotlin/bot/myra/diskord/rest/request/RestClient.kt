package bot.myra.diskord.rest.request

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.utilities.FileFormats
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.rest.Route
import bot.myra.diskord.rest.request.error.RecoverableException
import bot.myra.diskord.rest.request.error.RestStatus
import bot.myra.diskord.rest.request.error.rateLimits.RateLimitWorker
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import org.slf4j.LoggerFactory

/**
 * Http client for executing rest requests.
 */
class RestClient(val diskord: Diskord) {

    val logger = LoggerFactory.getLogger(RestClient::class.java)
    private val httpClient: HttpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            connectTimeoutMillis = 10000
            requestTimeoutMillis = 10000
        }
        expectSuccess = false // Disables throwing exceptions
        defaultRequest {
            url { protocol = URLProtocol.HTTPS }
            header("Authorization", "Bot ${diskord.token}")
        }
    }

    /**
     * Executes a http request by using stacktrace recovery.
     *
     * @param R The response type.
     * @param route The route to execute.
     * @param request Detailed information about the request.
     * @return Returns the response as [R].
     */
    suspend fun <R> execute(route: Route<R>, request: suspend HttpRequest<R>.() -> Unit = {}): Result<R> {
        return handle(HttpRequest(route).apply { request.invoke(this) })
    }

    suspend fun <R> execute(request: HttpRequest<R>): Result<R> {
        return handle(request)
    }

    private suspend fun <R> executeRequest(route: Route<R>, request: HttpRequest<R>.() -> Unit = {}): Result<R> {
        return executeRequest(HttpRequest(route).apply(request))
    }

    private suspend fun <R> executeRequest(req: HttpRequest<R>): Result<R> {
        logger.debug("Rest >>> ${req.route.httpMethod}: ${req.getFullPath()} - ${req.json}")

        val response = if (req.attachments.isEmpty()) bodyRequest(req) // Request doesn't contain files
        else formDataRequest(req, req.json!!, req.attachments) // Request needs to send files

        logger.debug("Rest <<< ${response.bodyAsText()}")

        val status = response.status
        return if (status.isSuccess()) {
            val value = when (req.route.serializer == Unit.serializer()) {
                true -> Unit as R
                false -> JSON.decodeFromString(req.route.serializer, response.bodyAsText())
            }
            Result(value, status, null)
        } else {
            val error = JSON.decodeFromString<JsonElement>(response.bodyAsText())
            val restStatus = RestStatus.getByStatusCode(status.value)
            if (restStatus is RestStatus.TooManyRequests) RateLimitWorker(diskord).queue(req, JSON.decodeFromJsonElement(error))
            return Result(null, status, error)
        }
    }


    /**
     * Executes a http request with an optional body.
     *
     * @param req Holding all information about the request.
     * @return Returns the response as a [Result].
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
            logger.debug("Retrying")
            return bodyRequest(req)
        }
    }

    /**
     * Responsible to keep the stacktrace after it got lost through Ktor's
     * [io.ktor.util.pipeline.SuspendFunctionGun]
     */
    suspend fun <T> handle(request: HttpRequest<T>): Result<T> {
        val dummyException = RecoverableException()
        return try {
            executeRequest(request)
        } catch (e: Exception) {
            throw dummyException.apply {
                initCause(e)
            }
        }
    }

    /**
     * Used to send files with messages. See [Discords documentation](https://discord.com/developers/docs/reference#uploading-files)
     * or the [documentation of ktor to upload a file](https://ktor.io/docs/request.html#upload_file).
     *
     * @param request Http request information.
     * @param json Optional json body.
     * @param files File to u upload.
     * @return Returns the response as a [Result].
     */
    private suspend fun formDataRequest(request: HttpRequest<*>, json: String, files: List<File>): HttpResponse =
        httpClient.submitFormWithBinaryData(
            formData {
                append("payload_json", json)
                files.forEachIndexed { n, file ->
                    append("files[$n]", file.bytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}.${file.type.extension}\"")
                        append(HttpHeaders.ContentType, getContentType(file).contentType)
                    })
                }
            }
        ) {
            url(request.getFullPath())
            method = request.route.httpMethod
        }

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