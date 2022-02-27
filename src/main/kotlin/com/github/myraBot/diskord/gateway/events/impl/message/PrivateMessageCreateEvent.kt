package com.github.myraBot.diskord.gateway.events.impl.message

import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.message.Message
import kotlinx.coroutines.Deferred

class PrivateMessageCreateEvent(message: Message) : GenericMessageCreateEvent(message) {
    fun getChannelAsync(): Deferred<DmChannel?> = message.getChannelAsAsync()
}