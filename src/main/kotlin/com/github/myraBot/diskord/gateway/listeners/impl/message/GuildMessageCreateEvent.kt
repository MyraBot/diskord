package com.github.myraBot.diskord.gateway.listeners.impl.message

import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.message.Message
import kotlinx.coroutines.runBlocking

class GuildMessageCreateEvent(message: Message) : MessageCreateEvent(message) {
    val guild: Guild get() = runBlocking { message.getGuild().awaitNonNull() }
    val member:Member get() = runBlocking { message.member.awaitNonNull() }
    val channel: TextChannel get() = runBlocking { message.getChannelAs<TextChannel>().awaitNonNull() }
}