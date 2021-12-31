package diskord.rest.behaviors

import diskord.common.entities.message.Message
import diskord.rest.Endpoints
import diskord.rest.builders.MessageBuilder
import diskord.utilities.JSON
import kotlinx.serialization.encodeToString
import java.net.URLEncoder

interface MessageBehavior : GetTextChannelBehavior, Entity {
    val message: Message

    /**
     * [Documentation](https://discord.com/developers/docs/resources/channel#edit-message)
     * Edits the current [Message] with the new message parameter.
     *
     * @param messageBuilder The new message.
     * @return Returns the new message.
     */
    suspend fun edit(messageBuilder: MessageBuilder): Message {
        val json = JSON.encodeToString(messageBuilder)
        return Endpoints.editMessage.executeNonNull(json) {
            arg("channel.id", message.channelId)
            arg("message.id", message.id)
        }
    }

    suspend fun edit(messageBuilder: MessageBuilder.() -> Unit): Message = edit(message.asBuilder().apply(messageBuilder))

    suspend fun addReaction(emoji: String) {
        Endpoints.addReaction.execute {
            arg("channel.id", message.channelId)
            arg("message.id", id)
            arg("emoji", URLEncoder.encode(emoji, "utf-8"))
        }
    }

}