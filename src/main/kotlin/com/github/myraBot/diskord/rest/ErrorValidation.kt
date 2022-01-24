package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.Diskord
import io.ktor.http.*
import kotlin.jvm.Throws

object ErrorValidation {

    fun validateResponse(status: HttpStatusCode) = when (status) {
        HttpStatusCode.OK -> Unit
        HttpStatusCode.Created -> Unit
        HttpStatusCode.NoContent -> Unit

        HttpStatusCode.NotModified -> Diskord.errorHandler.onEntityModifyException()
        HttpStatusCode.BadRequest -> Diskord.errorHandler.onBadRequest()
        HttpStatusCode.Unauthorized -> throw Exception() // Internal exception
        HttpStatusCode.Forbidden -> Diskord.errorHandler.onMissingPermissions()
        HttpStatusCode.NotFound -> Diskord.errorHandler.onNotFoundException()
        HttpStatusCode.MethodNotAllowed -> throw Exception() // Internal exception
        HttpStatusCode.TooManyRequests -> Diskord.errorHandler.onRateLimit()
        else -> throw UnknownError()
    }

}