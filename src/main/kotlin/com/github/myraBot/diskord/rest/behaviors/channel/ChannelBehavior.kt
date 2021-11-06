package com.github.myraBot.diskord.rest.behaviors.channel

import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.behaviors.Entity
import com.github.myraBot.diskord.rest.builders.MessageBuilder
import com.github.myraBot.diskord.utilities.JSON
import kotlinx.serialization.encodeToString

interface ChannelBehavior : Entity {

    suspend fun send(message: suspend MessageBuilder.() -> Unit): Message {
        val json = JSON.encodeToString(MessageBuilder().also { message.invoke(it) })
        return Endpoints.createMessage.execute(json) { arg("channel.id", id) }
    }

}