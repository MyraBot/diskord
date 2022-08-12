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
        if (GatewayIntent.GUILD_MESSAGES !in Diskord.intents) throw MissingIntentException(MessageCreateEvent::class, GatewayIntent.GUILD_MESSAGES)
    }

    @ListenTo(MessageCreateEvent::class)
    fun onMessageCreate(event: MessageCreateEvent) = update(event.message)

    @ListenTo(MessageUpdateEvent::class)
    fun onMessageUpdate(event: MessageUpdateEvent) {
        /*
        val message = get(event.message.id) ?: Diskord.getMessage(event.message.channelId, event.message.id) ?: return
        val updatedMessage = Message(
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
        */
        update(event.message)
    }

    @ListenTo(MessageDeleteEvent::class)
    fun onMessageDelete(event: MessageDeleteEvent) = remove(event.message.id)

    @ListenTo(BulkMessageDeleteEvent::class)
    fun onBulkMessageDelete(event: BulkMessageDeleteEvent) = event.message.ids.onEach { remove(it) }

}

class DisabledMessageCachePolicy : MessageCachePolicy()

abstract class MessageCachePolicy : GenericCachePolicy<String, Message>()