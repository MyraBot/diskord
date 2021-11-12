package com.github.myraBot.diskord.rest.behaviors.channel

import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.behaviors.Entity
import com.github.myraBot.diskord.rest.builders.MessageBuilder
import com.github.myraBot.diskord.utilities.JSON
import kotlinx.serialization.encodeToString

interface ChannelBehavior : Entity {

    suspend fun send(vararg files: File = emptyArray(), message: suspend MessageBuilder.() -> Unit): Message = send(files = files, message = MessageBuilder().also { message.invoke(it) })

    suspend fun send(vararg files: File = emptyArray(), message: MessageBuilder): Message {
        val json = JSON.encodeToString(message)
        return Endpoints.createMessage.executeNonNull(json, files.toList()) { arg("channel.id", id) }
    }

}