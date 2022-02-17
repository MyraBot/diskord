package com.github.myraBot.diskord.rest.request

import com.github.myraBot.diskord.common.JSON
import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.common.utilities.REST_CLIENT
import com.github.myraBot.diskord.common.utilities.kTrace
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.error.validateResponse
import com.github.myraBot.diskord.utilities.FileFormats
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.builtins.serializer

interface HttpRequestClient<R> {

    /**
     * Executes a http request to the saved path of [data].
     *
     * @param data information about the to be executed [HttpRequest].
     */
    suspend fun execute(data: HttpRequest<R>): R? {
        var route = Endpoints.baseUrl + data.route.path
        data.arguments.entries.forEach { route = route.replace("{${it.key}}", it.value.toString()) }

        val response = if (data.attachments.isEmpty()) bodyRequest(route, data.route.httpMethod, data.json, data.logReason) // Request doesn't contain files
        else formDataRequest(route, data.json!!, data.attachments) // Request needs to send files

        kTrace(this::class) { "Rest <<< ${response.readText()}" }

        val errorValidation = validateResponse(response)
        if (errorValidation.exception != null) throw errorValidation.exception
        if (errorValidation.returnNull) return null

        if (data.route.serializer == Unit.serializer()) return Unit as R
        return JSON.decodeFromString(data.route.serializer, response.readText()).also {
            data.route.cache?.run {
                this.invoke(it, data.arguments)
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
    private suspend fun bodyRequest(route: String, httpMethod: HttpMethod, json: String?, reason: String?): HttpResponse {
        return try {
            REST_CLIENT.request(route) {
                method = httpMethod
                reason?.let { headers { header("X-Audit-Log-Reason", it) } }
                json?.let {
                    contentType(ContentType.Application.Json)
                    body = it
                }
            }
        } catch (timeout: HttpRequestTimeoutException) {
            bodyRequest(route, httpMethod, json, reason)
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
    private suspend fun formDataRequest(route: String, json: String, files: List<File>): HttpResponse {
        return try {
            REST_CLIENT.submitFormWithBinaryData(
                url = route,
                formData = formData {
                    append("payload_json", json)
                    files.forEachIndexed { n, file ->

                        append("files[$n]", file.bytes, Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"${file.name}.${file.type.extension}\"")
                            append(HttpHeaders.ContentType, getContentType(file))
                        })

                    }
                }
            )
        } catch (timeout: HttpRequestTimeoutException) {
            formDataRequest(route, json, files)
        }
    }

    private fun getContentType(file: File): ContentType {
        return when (file.type) {
            FileFormats.JPEG -> ContentType.Image.JPEG
            FileFormats.PNG -> ContentType.Image.PNG
            FileFormats.GIF -> ContentType.Image.GIF
            else -> throw Exception("The provided file type isn't registered as a content type")
        }
    }

}