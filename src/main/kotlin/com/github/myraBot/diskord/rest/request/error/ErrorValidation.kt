package com.github.myraBot.diskord.rest.request.error

import io.ktor.client.statement.*
import io.ktor.http.*

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

fun validateResponse(response: HttpResponse): ErrorValidation = when (response.status) {
    HttpStatusCode.OK -> ErrorValidation.success()
    HttpStatusCode.Created -> ErrorValidation.success()
    HttpStatusCode.NoContent -> ErrorValidation.success()

    HttpStatusCode.NotModified -> ErrorValidation.failure(EntityModifyException())
    HttpStatusCode.BadRequest -> ErrorValidation.failure(BadReqException())
    HttpStatusCode.Unauthorized -> throw Exception() // Internal exception
    HttpStatusCode.Forbidden -> ErrorValidation.failure(MissingPermissionsException())
    HttpStatusCode.NotFound -> ErrorValidation.failure()
    HttpStatusCode.MethodNotAllowed -> throw Exception() // Internal exception
    HttpStatusCode.TooManyRequests -> ErrorValidation.failure(RateLimitException())
    else -> throw UnknownError()
}
