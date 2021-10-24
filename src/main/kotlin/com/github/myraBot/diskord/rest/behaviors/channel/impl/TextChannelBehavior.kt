package com.github.myraBot.diskord.rest.behaviors.channel.impl

import com.github.m5rian.discord.JSON
import com.github.myraBot.diskord.common.entityData.message.MessageData
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.behaviors.channel.ChannelBehavior
import com.github.myraBot.diskord.rest.builders.MessageBuilder
import kotlinx.serialization.encodeToString

interface TextChannelBehavior : ChannelBehavior {

    suspend fun send(builder: MessageBuilder.() -> Unit): MessageData {
        val message = MessageBuilder().apply(builder)
        val json = JSON.encodeToString(message)
        return Endpoints.createMessage.execute(json) { arg("channel.id", id) }
    }

}