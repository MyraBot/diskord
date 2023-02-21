package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.entities.message.MessageData
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.message.BulkMessageDeleteEvent
import bot.myra.diskord.gateway.events.impl.message.MessageDeleteEvent
import bot.myra.diskord.gateway.events.impl.message.MessageUpdateEvent
import bot.myra.diskord.gateway.events.impl.message.create.GenericMessageCreateEvent
import bot.myra.diskord.gateway.events.types.Event

class MutableMessageCachePolicy : MessageCachePolicy() {

    private fun checkIntents(event: Event) {
        if (GatewayIntent.GUILD_MESSAGES !in event.diskord.intents) {
            throw MissingIntentException(MessageCachePolicy::class, GatewayIntent.GUILD_MESSAGES)
        }
    }

    @ListenTo(GenericMessageCreateEvent::class)
    suspend fun onMessageCreate(event: GenericMessageCreateEvent) {
        checkIntents(event)
        update(event.message.data)
    }

    @ListenTo(MessageUpdateEvent::class)
    suspend fun onMessageUpdate(event: MessageUpdateEvent) {
        checkIntents(event)

        val message = get(event.message.id).value ?: return
        val updatedMessage = MessageData(
            id = message.id,
            channelId = message.channelId,
            guildId = message.guildId,
            user = message.user,
            memberData = message.memberData,
            content = event.message.content ?: message.content,
            timestamp = message.timestamp,
            edited = event.message.edited,
            tts = message.tts,
            mentionsEveryone = message.mentionsEveryone,
            mentionedUsers = message.mentionedUsers,
            mentionedRoles = message.mentionedRoles,
            mentionedChannels = message.mentionedChannels,
            attachments = event.message.attachments ?: message.attachments,
            embeds = event.message.embeds?.toMutableList() ?: message.embeds,
            reactions = message.reactions,
            pinned = message.pinned,
            webhookId = message.webhookId,
            type = message.type,
            flags = message.flags,
            components = event.message.components?.toMutableList() ?: message.components
        )
        update(updatedMessage)
    }

    @ListenTo(MessageDeleteEvent::class)
    suspend fun onMessageDelete(event: MessageDeleteEvent) {
        checkIntents(event)
        remove(event.message.id)
    }

    @ListenTo(BulkMessageDeleteEvent::class)
    suspend fun onBulkMessageDelete(event: BulkMessageDeleteEvent) {
        checkIntents(event)
        event.message.ids.onEach { remove(it) }
    }

}

class DisabledMessageCachePolicy : MessageCachePolicy()

abstract class MessageCachePolicy : GenericCachePolicy<String, MessageData>() {
    override fun getAsKey(value: MessageData): String = value.id
}