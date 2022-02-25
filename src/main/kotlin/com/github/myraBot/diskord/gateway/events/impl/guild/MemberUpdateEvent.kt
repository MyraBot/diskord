package com.github.myraBot.diskord.gateway.events.impl.guild

import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.serialization.Serializable

@Serializable
data class MemberUpdateEvent(
        val member: Member,
) : Event() {
    val guild: Guild get() = member.guild
}
