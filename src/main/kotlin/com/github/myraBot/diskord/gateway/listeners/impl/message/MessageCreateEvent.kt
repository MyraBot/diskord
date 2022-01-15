package com.github.myraBot.diskord.gateway.listeners.impl.message

import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.coroutines.runBlocking

data class MessageCreateEvent(
        val message: Message,
) : Event() {
    val content = message.content
    val isWebhook = message.isWebhook
    val isSystem = message.isSystem
    val user = message.user
    val channel: TextChannel get() = runBlocking { message.getChannelAs<TextChannel>().awaitNonNull() }
    val guild: Guild? get() = runBlocking { message.getGuild().await() }
    val member = message.member

    override suspend fun call() {
        if (guild == null) PrivateMessageCreateEvent(message).call()
        else GuildMessageCreateEvent(message).call()

        super.call()
    }

}