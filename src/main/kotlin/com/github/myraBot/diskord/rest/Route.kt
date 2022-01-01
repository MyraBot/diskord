package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.utilities.FileFormats
import com.github.myraBot.diskord.utilities.JSON
import com.github.myraBot.diskord.utilities.REST_CLIENT
import com.github.myraBot.diskord.utilities.logging.trace
import com.github.myraBot.diskord.utilities.logging.error
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer

class Route<R>(private val httpMethod: HttpMethod, private val path: String, private val serializer: KSerializer<R>) {

    /**
     * Executes a http request to [path].
     *
     * @param json Optional json data.
     * @param files Optional files. When files are set the method calls [dataRequest], which is a multipart/form-data request.
     * @param argBuilder Path arguments.
     * @return Returns a nullable response as [R].
     */
    suspend fun execute(json: String? = null, files: List<File> = emptyList(), argBuilder: RouteArguments.() -> Unit = {}): R? {
        val request = requestRouter(json, RouteArguments().apply(argBuilder), files)
        val response = request.readText()
        trace(this::class) { "Rest response = $response" }

        if (request.status != HttpStatusCode.OK) return null
        if (serializer == Unit.serializer()) return Unit as R
        return JSON.decodeFromString(serializer, response)
    }

    /**
     * Executes a http request to [path]. This method will throw an exception when the response is null.
     * For nullable requests use [execute].
     *
     * @param json Optional json data.
     * @param files Optional files. When files are set the method calls [dataRequest], which is a multipart/form-data request.
     * @param argBuilder Path arguments.
     * @return Returns a response as [R]. If the response is null, throws an exception.
     */
    suspend fun executeNonNull(json: String? = null, files: List<File> = emptyList(), argBuilder: RouteArguments.() -> Unit = {}): R {
        val response = requestRouter(json, RouteArguments().apply(argBuilder), files)
        val resText = response.readText()
        trace(this::class) { "Rest response = $resText" }

        if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.NoContent) {
            error(this::class) { "An error occurred while executing a rest action \"$path\":" }
            error(this::class) { "Response: $resText" }
            throw Exception()
        }
        if (serializer == Unit.serializer()) return Unit as R
        return JSON.decodeFromString(serializer, resText)
    }

    /**
     * Executes the Http request.
     * This method decides if the request requires sending files.
     *
     * @param json Optional json data to send with the request.
     * @param args Route path variables to replace.
     * @param files Optional files.
     * @return Return a [HttpResponse] of the executed request.
     */
    private suspend fun requestRouter(json: String? = null, args: RouteArguments, files: List<File> = emptyList()): HttpResponse {
        var route = Endpoints.baseUrl + path
        args.entries.forEach { route = route.replace("{${it.first}}", it.second) }

        return if (files.isEmpty()) request(route, json) // Request doesn't contain files
        else dataRequest(route, json!!, files) // Request needs to send files
    }

    /**
     * Executes a normal http request. The http method is determined by [httpMethod].
     *
     * @param route The route to execute.
     * @param json Optional json body.
     * @return Returns the response as a [HttpResponse].
     */
    private suspend fun request(route: String, json: String?): HttpResponse {
        return try {
            REST_CLIENT.request(route) {
                method = httpMethod
                json?.let {
                    contentType(ContentType.Application.Json)
                    body = it
                }
            }
        } catch (timeout: HttpRequestTimeoutException) {
            request(route, json)
        }
    }

    /**
     * Used to send files with messages. See [Discords documentation](https://discord.com/developers/docs/reference#uploading-files)
     * or the [documentation of ktor to upload a file](https://ktor.io/docs/request.html#upload_file).
     *
     *
     * @param route The route to execute.
     * @param json Optional json body.
     * @param files Optional files which to upload.
     * @return Returns the response as a [HttpResponse].
     */
    private suspend fun dataRequest(route: String, json: String, files: List<File>): HttpResponse {
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
            request(route, json)
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