package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.utilities.JSON
import kotlinx.serialization.encodeToString
import java.net.URL
import java.net.URLEncoder

interface MessageBehavior : GetTextChannelBehavior, Entity {

    val channelId: String

    /**
     * [Documentation](https://discord.com/developers/docs/resources/channel#edit-message)
     * Edits the current [Message] with the new message parameter.
     *
     * @param message The new message.
     * @return Returns the new message.
     */
    suspend fun edit(message: Message): Message {
        val json = JSON.encodeToString(message)
        return Endpoints.editMessage.execute(json) {
            arg("channel.id", message.channelId)
            arg("message.id", message.id)
        }
    }

    suspend fun edit(message: (Message) -> Message): Message = edit(message)

    suspend fun addReaction(emoji: String) {
        Endpoints.addReaction.execute {
            arg("channel.id", channelId)
            arg("message.id", id)
            arg("emoji", URLEncoder.encode(emoji,"utf-8"))
        }
    }

}