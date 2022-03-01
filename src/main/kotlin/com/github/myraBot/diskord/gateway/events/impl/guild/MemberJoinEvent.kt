package com.github.myraBot.diskord.gateway.events.impl.guild

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberJoinEvent(
    val member: Member
) : Event() {

    fun getGuildAsync(): Deferred<Guild?> = Diskord.getGuildAsync(member.guildId)

}