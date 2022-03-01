package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.builders.MessageBuilder
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred
import java.net.URLEncoder

@Suppress("unused")
interface MessageBehavior : GetTextChannelBehavior, Entity {
    val message: Message

    /**
     * [Documentation](https://discord.com/developers/docs/resources/channel#edit-message)
     * Edits the current [Message] with the new message parameter.
     *
     * @param messageBuilder The new message.
     * @return Returns the new message.
     */
    fun editAsync(messageBuilder: MessageBuilder): Deferred<Message> {
        return RestClient.executeAsync(Endpoints.editMessage) {
            json = messageBuilder.toJson()
            arguments {
                arg("channel.id", message.channelId)
                arg("message.id", message.id)
            }
        }
    }

    fun editAsync(messageBuilder: MessageBuilder.() -> Unit): Deferred<Message> = editAsync(message.asBuilder().apply(messageBuilder))

    fun addReactionAsync(emoji: String): Deferred<Unit> {
        return RestClient.executeAsync(Endpoints.addReaction) {
            arguments {
                arg("channel.id", message.channelId)
                arg("message.id", id)
                arg("emoji", URLEncoder.encode(emoji, "utf-8"))
            }
        }
    }

}