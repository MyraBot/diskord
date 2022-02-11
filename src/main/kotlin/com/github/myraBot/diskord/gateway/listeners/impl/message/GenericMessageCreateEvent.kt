package com.github.myraBot.diskord.gateway.listeners.impl.message

import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.common.isMissing

class GenericMessageCreateEvent(
    message: Message
) : MessageCreateEvent(message) {

    override suspend fun call() {
        if (message.guildId.isMissing()) PrivateMessageCreateEvent(message).call()
        else GuildMessageCreateEvent(message).call()
        super.call()
    }

}