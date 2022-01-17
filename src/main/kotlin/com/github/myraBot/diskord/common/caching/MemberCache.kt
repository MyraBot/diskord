package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.gateway.listeners.ListenTo
import com.github.myraBot.diskord.gateway.listeners.impl.guild.MemberUpdateEvent
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.Promise

object MemberCache : Cache<DoubleKey, Member>(
    retrieve = { key ->
        Promise.of(Endpoints.getGuildMember) {
            arg("guild.id", key.first)
            arg("user.id", key.second)
        }.map { data -> data?.let { Member.withUserInMember(it, key.second) } }
    }
) {
    @ListenTo(MemberUpdateEvent::class)
    fun onMemberUpdate(event: MemberUpdateEvent) {
        cache[DoubleKey(event.guild.id, event.member.id)] = event.member
    }
}

