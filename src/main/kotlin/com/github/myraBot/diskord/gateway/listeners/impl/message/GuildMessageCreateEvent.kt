package com.github.myraBot.diskord.gateway.listeners.impl.message

import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.coroutines.runBlocking

data class GuildMessageCreateEvent(
        val message: Message,
) : Event() {
    val content = message.content
    val isWebhook = message.isWebhook
    val isSystem = message.isSystem
    val user = message.user
    val channel: TextChannel get() = runBlocking { message.getChannelAs<TextChannel>().awaitNonNull() }
    val guild: Guild get() = runBlocking { message.getGuild().awaitNonNull() }
    val member: Member get() = runBlocking { message.member.awaitNonNull() }
}