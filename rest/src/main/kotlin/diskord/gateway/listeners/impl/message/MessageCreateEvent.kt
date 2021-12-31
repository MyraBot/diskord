package diskord.gateway.listeners.impl.message

import diskord.common.entities.message.Message
import diskord.common.entities.message.member
import diskord.gateway.listeners.Event

data class MessageCreateEvent(
        val message: Message,
) : Event() {
    val content = message.content
    val isWebhook = message.isWebhook
    val isSystem = message.isSystem
    val user = message.user
    val channel = message.channel
    val guild = message.guild
    val member = message.member

    override suspend fun call() {
        if (guild == null) PrivateMessageCreateEvent(message).call()
        else GuildMessageCreateEvent(message).call()

        super.call()
    }

}