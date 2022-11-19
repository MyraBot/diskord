package bot.myra.diskord.rest.request.error

import bot.myra.diskord.gateway.events.types.Event

open class ErrorHandler {
    open fun onException(event: Event, exception: Throwable) = exception.printStackTrace()
}