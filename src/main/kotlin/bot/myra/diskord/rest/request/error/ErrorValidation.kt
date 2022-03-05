package bot.myra.diskord.rest.request.error

import bot.myra.diskord.common.JSON
import bot.myra.diskord.common.utilities.string
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject

data class ErrorValidation(
    val exception: Exception?,
    val returnNull: Boolean
) {
    companion object {
        fun success(): ErrorValidation = ErrorValidation(exception = null, returnNull = false)
        fun failure(): ErrorValidation = ErrorValidation(exception = null, returnNull = true)
        fun failure(exception: Exception): ErrorValidation = ErrorValidation(exception = exception, returnNull = false)
    }
}

suspend fun validateResponse(response: HttpResponse): ErrorValidation {
    return if (response.status.isSuccess()) {
        ErrorValidation.success()
    } else {
        val error = JSON.decodeFromString<JsonObject>(response.readText())["message"]?.string ?: "No error message provided"
        when (response.status) {
            HttpStatusCode.NotModified -> ErrorValidation.failure(EntityModifyException(error))
            HttpStatusCode.BadRequest -> ErrorValidation.failure(BadReqException(error))
            HttpStatusCode.Unauthorized -> throw Exception() // Internal exception
            HttpStatusCode.Forbidden -> ErrorValidation.failure()
            HttpStatusCode.NotFound -> ErrorValidation.failure()
            HttpStatusCode.MethodNotAllowed -> throw Exception() // Internal exception
            HttpStatusCode.TooManyRequests -> ErrorValidation.failure(RateLimitException(error))
            else -> throw UnknownError()
        }
    }
}