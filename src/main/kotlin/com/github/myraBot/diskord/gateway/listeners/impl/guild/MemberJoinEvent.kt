package com.github.myraBot.diskord.gateway.listeners.impl.guild

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.coroutines.runBlocking

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberJoinEvent(
    val member: Member
) : Event() {
    val guild: Guild get() = runBlocking { Diskord.getGuild(member.guildId).awaitNonNull() }
}