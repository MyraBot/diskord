package com.github.myraBot.diskord.rest.behaviors.channel

import com.github.m5rian.discord.JSON
import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.behaviors.Entity
import com.github.myraBot.diskord.rest.builders.MessageBuilder
import kotlinx.serialization.encodeToString

interface ChannelBehavior : Entity {

    suspend fun send(message: MessageBuilder.() -> Unit): Message {
        val json = JSON.encodeToString(MessageBuilder().apply(message))
        val messageData = Endpoints.createMessage.execute(json) { arg("channel.id", id) }
        return Message(messageData)
    }

}