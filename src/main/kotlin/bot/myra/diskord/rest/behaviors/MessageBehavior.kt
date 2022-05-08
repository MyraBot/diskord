package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.modifiers.message.components.MessageModifier
import bot.myra.diskord.rest.request.RestClient
import java.net.URLEncoder

@Suppress("unused")
interface MessageBehavior : GetTextChannelBehavior, Entity {

    val message: Message

    /**
     * [Documentation](https://discord.com/developers/docs/resources/channel#edit-message)
     * Edits the current [Message] with the new message parameter.
     *
     * @param messageModifier The new message.
     * @return Returns the new message.
     */
    suspend fun edit(messageModifier: MessageModifier): Message {
        return RestClient.execute(Endpoints.editMessage) {
            json = messageModifier.toJson()
            arguments {
                arg("channel.id", message.channelId)
                arg("message.id", message.id)
            }
        }
    }

    suspend fun edit(messageModifier: MessageModifier.() -> Unit): Message = edit(message.asModifier().apply(messageModifier))

    suspend fun delete() {
        RestClient.execute(Endpoints.deleteMessage) {
            arguments {
                arg("channel.id", message.channelId)
                arg("message.id", message.id)
            }
        }
    }

    suspend fun addReaction(emoji: String) {
        return RestClient.execute(Endpoints.addReaction) {
            arguments {
                arg("channel.id", message.channelId)
                arg("message.id", id)
                arg("emoji", URLEncoder.encode(emoji, "utf-8"))
            }
        }
    }

}