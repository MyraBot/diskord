package com.github.myraBot.diskord.gateway.listeners.impl.message

import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.message.Message
import kotlinx.coroutines.runBlocking

class PrivateMessageCreateEvent(message: Message) : GenericMessageCreateEvent(message) {
    val channel: DmChannel get() = runBlocking { message.getChannelAs<DmChannel>().awaitNonNull() }
}