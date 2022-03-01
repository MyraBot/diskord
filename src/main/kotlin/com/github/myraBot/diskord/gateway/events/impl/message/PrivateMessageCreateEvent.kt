package com.github.myraBot.diskord.gateway.events.impl.message

import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred

class PrivateMessageCreateEvent(
    val message: Message
) : Event() {

    fun getChannelAsync(): Deferred<DmChannel?> = message.getChannelAsAsync()

}