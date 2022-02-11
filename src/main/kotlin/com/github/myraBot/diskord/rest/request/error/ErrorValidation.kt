package com.github.myraBot.diskord.rest.request.error

import io.ktor.http.*

data class ErrorValidation(
    val throwError: Boolean,
    val errorCallback: (ErrorHandler) -> Unit,
    val returnNull: Boolean
) {
    companion object {
        fun success(): ErrorValidation = ErrorValidation(throwError = false, errorCallback = {}, returnNull = false)
        fun failure(): ErrorValidation = ErrorValidation(throwError = true, errorCallback = {}, returnNull = true)
        fun failure(callback: (ErrorHandler) -> Unit): ErrorValidation = ErrorValidation(throwError = true, errorCallback = callback, returnNull = false)
    }
}

fun validateResponse(status: HttpStatusCode): ErrorValidation = when (status) {
    HttpStatusCode.OK -> ErrorValidation.success()
    HttpStatusCode.Created -> ErrorValidation.success()
    HttpStatusCode.NoContent -> ErrorValidation.success()

    HttpStatusCode.NotModified -> ErrorValidation.failure { it.onEntityModifyException() }
    HttpStatusCode.BadRequest -> ErrorValidation.failure { it.onBadRequest() }
    HttpStatusCode.Unauthorized -> throw Exception() // Internal exception
    HttpStatusCode.Forbidden -> ErrorValidation.failure { it.onMissingPermissions() }
    HttpStatusCode.NotFound -> ErrorValidation.failure()
    HttpStatusCode.MethodNotAllowed -> throw Exception() // Internal exception
    HttpStatusCode.TooManyRequests -> ErrorValidation.failure { it.onRateLimit() }
    else -> throw UnknownError()
}
