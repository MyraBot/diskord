package com.github.myraBot.diskord.gateway.listeners.impl.message

import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.coroutines.runBlocking

data class PrivateMessageCreateEvent(
        val message: Message,
) : Event() {
    val content = message.content
    val isWebhook = message.isWebhook
    val isSystem = message.isSystem
    val user = message.user
    val channel: DmChannel get() = runBlocking { message.getChannelAs<DmChannel>().awaitNonNull() }
}