package com.github.myraBot.diskord.gateway.events.impl.guild

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.coroutines.runBlocking

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberJoinEvent(
    val member: Member
) : Event() {
    val guild: Guild get() = runBlocking { Diskord.getGuild(member.guildId).awaitNonNull() }
}