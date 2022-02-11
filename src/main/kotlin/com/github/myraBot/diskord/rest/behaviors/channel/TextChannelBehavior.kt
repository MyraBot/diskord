package com.github.myraBot.diskord.rest.behaviors.channel

import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.builders.MessageBuilder
import com.github.myraBot.diskord.rest.request.promises.Promise
import com.github.myraBot.diskord.common.JSON
import com.github.myraBot.diskord.rest.transform
import kotlinx.serialization.encodeToString

@Suppress("unused")
interface TextChannelBehavior {

    val data: ChannelData

    suspend fun send(vararg files: File = emptyArray(), message: MessageBuilder): Promise<Message> {
        val json = JSON.encodeToString(message.transform())
        return Promise.of(Endpoints.createMessage, json, files.toList()) { arg("channel.id", data.id) }
    }

    suspend fun send(vararg files: File = emptyArray(), message: suspend MessageBuilder.() -> Unit): Promise<Message> {
        return send(files = files, message = MessageBuilder().also { message.invoke(it) })
    }

    suspend fun send(vararg files: File): Promise<Message> {
        return send(files = files, message = MessageBuilder())
    }

}