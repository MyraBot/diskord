package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.message.BulkMessageDeleteEvent
import bot.myra.diskord.gateway.events.impl.message.MessageCreateEvent
import bot.myra.diskord.gateway.events.impl.message.MessageDeleteEvent
import bot.myra.diskord.gateway.events.impl.message.MessageUpdateEvent

class MutableMessageCachePolicy : MessageCachePolicy() {
    init {
        if (GatewayIntent.GUILD_MESSAGES !in Diskord.intents) {
            throw MissingIntentException(Diskord.cachePolicy.message, GatewayIntent.GUILD_MESSAGES)
        }
    }
}

class DisabledMessageCachePolicy : MessageCachePolicy()

abstract class MessageCachePolicy : GenericCachePolicy<String, Message>() {

    @ListenTo(MessageCreateEvent::class)
    fun onMessageCreate(event: MessageCreateEvent) = update(event.message)

    @ListenTo(MessageUpdateEvent::class)
    fun onMessageUpdate(event: MessageUpdateEvent) = update(event.message)

    @ListenTo(MessageDeleteEvent::class)
    fun onMessageDelete(event: MessageDeleteEvent) = remove(event.message.id)

    @ListenTo(BulkMessageDeleteEvent::class)
    fun onBulkMessageDelete(event: BulkMessageDeleteEvent) = event.message.ids.onEach { remove(it) }

}