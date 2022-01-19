package com.github.myraBot.diskord.rest

import io.ktor.http.*
import kotlin.jvm.Throws

object ErrorValidation {

    @Throws(Exception::class)
    fun validateResponse(status: HttpStatusCode) = when (status) {
        HttpStatusCode.OK -> Unit
        HttpStatusCode.Created -> Unit
        HttpStatusCode.NoContent -> Unit

        HttpStatusCode.NotModified -> throw EntityModifyException()
        HttpStatusCode.BadRequest -> throw BadRequestException()
        HttpStatusCode.Unauthorized -> throw Exception() // Internal exception
        HttpStatusCode.Forbidden -> throw MissingPermissionsException()
        HttpStatusCode.NotFound -> throw UnknownEntityException()
        HttpStatusCode.MethodNotAllowed -> throw Exception() // Internal exception
        HttpStatusCode.TooManyRequests -> throw RateLimitException()
        else -> throw UnknownError()
    }

}

class EntityModifyException : Exception()
class BadRequestException : Exception()
class MissingPermissionsException : Exception()
class UnknownEntityException : Exception()
class RateLimitException : Exception()