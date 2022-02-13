package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.common.toJson
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.builders.MessageBuilder
import com.github.myraBot.diskord.rest.request.promises.Promise
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
    suspend fun edit(messageBuilder: MessageBuilder): Promise<Message> {
        return Promise.of(Endpoints.editMessage) {
            json = messageBuilder.toJson()
            arguments {
                arg("channel.id", message.channelId)
                arg("message.id", message.id)
            }
        }
    }

    suspend fun edit(messageBuilder: MessageBuilder.() -> Unit): Promise<Message> = edit(message.asBuilder().apply(messageBuilder))

    suspend fun addReaction(emoji: String): Promise<Unit> {
        return Promise.of(Endpoints.addReaction) {
            arguments {
                arg("channel.id", message.channelId)
                arg("message.id", id)
                arg("emoji", URLEncoder.encode(emoji, "utf-8"))
            }
        }
    }

}