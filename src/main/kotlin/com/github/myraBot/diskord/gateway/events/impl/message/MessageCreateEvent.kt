package com.github.myraBot.diskord.gateway.events.impl.message

import com.github.myraBot.diskord.common.entities.message.Message

class MessageCreateEvent(
    message: Message
) : GenericMessageCreateEvent(message) {

    override suspend fun call() {
        if (message.guildId.missing) PrivateMessageCreateEvent(message).call()
        else GuildMessageCreateEvent(message).call()
        super.call()
    }

}