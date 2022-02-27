package com.github.myraBot.diskord.gateway.events.impl.guild

import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred
import kotlinx.serialization.Serializable

@Serializable
data class MemberUpdateEvent(
    val member: Member,
) : Event() {

    fun getGuildAsync(): Deferred<Guild?> = member.getGuildAsync()

}
