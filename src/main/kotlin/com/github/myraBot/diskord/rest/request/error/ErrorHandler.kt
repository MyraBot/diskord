package com.github.myraBot.diskord.rest.request.error

import com.github.myraBot.diskord.gateway.events.Event

open class ErrorHandler {
    open fun onException(event: Event, exception: Throwable) = exception.printStackTrace()
}