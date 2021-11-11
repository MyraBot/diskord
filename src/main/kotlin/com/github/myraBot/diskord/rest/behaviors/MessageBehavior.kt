package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.utilities.JSON
import kotlinx.serialization.encodeToString

interface MessageBehavior : GetTextChannelBehavior {

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

}