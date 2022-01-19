package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.gateway.listeners.Event
import kotlin.jvm.Throws

interface ErrorHandler {
    @Throws(Exception::class)
    fun onEntityModifyException(event: Event, exception: EntityModifyException)
    @Throws(Exception::class)
    fun onBadRequest(event: Event, exception: BadRequestException)
    @Throws(Exception::class)
    fun onMissingPermissions(event: Event, exception: MissingPermissionsException)
    @Throws(Exception::class)
    fun onNotFoundException(event: Event, exception: UnknownEntityException)
    @Throws(Exception::class)
    fun onRateLimit(event: Event, exception: RateLimitException)
}

object DefaultErrorHandler : ErrorHandler {
    override fun onEntityModifyException(event: Event, exception: EntityModifyException) = exception.printStackTrace()
    override fun onBadRequest(event: Event, exception: BadRequestException) = exception.printStackTrace()
    override fun onMissingPermissions(event: Event, exception: MissingPermissionsException) = exception.printStackTrace()
    override fun onNotFoundException(event: Event, exception: UnknownEntityException) = exception.printStackTrace()
    override fun onRateLimit(event: Event, exception: RateLimitException) = exception.printStackTrace()
}