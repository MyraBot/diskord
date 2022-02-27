package com.github.myraBot.diskord.gateway.events.impl.message

import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.message.Message
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking

class GuildMessageCreateEvent(message: Message) : GenericMessageCreateEvent(message) {

    fun getGuildAsync(): Deferred<Guild?> = message.getGuildAsync()
    fun getMemberAsync():Deferred<Member> = message.getMemberAsync()
    fun getChannelAsync() : Deferred<TextChannel?> = message.getChannelAsAsync()

}