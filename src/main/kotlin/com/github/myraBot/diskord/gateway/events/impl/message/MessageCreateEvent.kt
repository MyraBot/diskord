package com.github.myraBot.diskord.gateway.events.impl.message

import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.serialization.Serializable

@Serializable
open class MessageCreateEvent(
    val message: Message
) : Event() {

    override suspend fun prepareEvent() {
        if (message.guildId.missing) PrivateMessageCreateEvent(message)
        else GuildMessageCreateEvent(message)
    }

}