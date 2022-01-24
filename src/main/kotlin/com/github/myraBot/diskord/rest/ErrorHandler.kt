package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.gateway.listeners.Event

interface ErrorHandler {
    fun onException(event: Event, exception: Throwable)

    // Http exceptions
    fun onEntityModifyException() {}
    fun onBadRequest() {}
    fun onMissingPermissions() {}
    fun onNotFoundException() {}
    fun onRateLimit() {}
}

object DefaultErrorHandler : ErrorHandler {
    override fun onException(event: Event, exception: Throwable) = exception.printStackTrace()
}