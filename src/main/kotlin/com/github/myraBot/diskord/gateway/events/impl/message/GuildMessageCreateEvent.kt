package com.github.myraBot.diskord.gateway.events.impl.message

import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred

@Suppress("unused")
class GuildMessageCreateEvent(
    val message: Message
) : Event() {

    fun getGuildAsync(): Deferred<Guild?> = message.getGuildAsync()
    fun getMemberAsync(): Deferred<Member> = message.getMemberAsync()
    fun getChannelAsync(): Deferred<TextChannel?> = message.getChannelAsAsync()

}