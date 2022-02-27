package com.github.myraBot.diskord.rest.behaviors.channel

import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.common.toJson
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.builders.MessageBuilder
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred
import com.github.myraBot.diskord.rest.transform

@Suppress("unused")
interface TextChannelBehavior {

    val data: ChannelData

    suspend fun sendAsync(vararg files: File = emptyArray(), message: MessageBuilder): Deferred<Message> {
        val msg = message.transform()
        return RestClient.executeAsync(Endpoints.createMessage) {
            json = msg.toJson()
            attachments = files.toList()
            arguments { arg("channel.id", data.id) }
        }
    }

    suspend fun sendAsync(vararg files: File = emptyArray(), message: suspend MessageBuilder.() -> Unit): Deferred<Message> {
        return sendAsync(files = files, message = MessageBuilder().also { message.invoke(it) })
    }

    suspend fun sendAsync(vararg files: File): Deferred<Message> {
        return sendAsync(files = files, message = MessageBuilder())
    }

}