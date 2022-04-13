package bot.myra.diskord.rest.request.error

import bot.myra.diskord.gateway.events.Event

open class ErrorHandler {
    open fun onException(event: Event, exception: DiscordRestException) = exception.printStackTrace()
}