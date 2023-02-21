package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.entities.message.MessageData
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.modifiers.message.components.MessageModifier
import bot.myra.diskord.rest.request.RestClient
import bot.myra.diskord.rest.request.Result
import java.net.URLEncoder

@Suppress("unused")
interface MessageBehavior : Entity {
    val data: MessageData

    /**
     * [Documentation](https://discord.com/developers/docs/resources/channel#edit-message)
     * Edits the current [Message] with the new message parameter.
     *
     * @param messageModifier The new message.
     * @return Returns the new message.
     */
    suspend fun edit(messageModifier: MessageModifier): Result<Message> = diskord.rest.execute(Endpoints.editMessage) {
        json = messageModifier.toJson()
        arguments {
            arg("channel.id", data.channelId)
            arg("message.id", data.id)
        }
    }.transformValue { Message(it, diskord) }

    suspend fun edit(messageModifier: suspend MessageModifier.() -> Unit) = edit(data.asModifier().apply { messageModifier.invoke(this) })

    suspend fun delete() = diskord.rest.execute(Endpoints.deleteMessage) {
        arguments {
            arg("channel.id", data.channelId)
            arg("message.id", data.id)
        }
    }

    suspend fun addReaction(emoji: String) = diskord.rest.execute(Endpoints.addReaction) {
        arguments {
            arg("channel.id", data.channelId)
            arg("message.id", id)
            arg("emoji", URLEncoder.encode(emoji, "utf-8"))
        }
    }

    suspend fun clearReactions() = diskord.rest.execute(Endpoints.deleteAllReactions) {
        arguments {
            arg("channel.id", data.channelId)
            arg("message.id", id)
        }
    }

}