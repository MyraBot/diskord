package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.gateway.events.EventListener
import com.github.myraBot.diskord.gateway.events.ListenTo
import com.github.myraBot.diskord.gateway.events.impl.guild.MemberUpdateEvent

object MemberCachingListener : EventListener {
    @ListenTo(MemberUpdateEvent::class)
    suspend fun onMemberUpdate(event: MemberUpdateEvent) {
        val guild = event.getGuildAsync().await()
        MemberCache.cache[DoubleKey(guild!!.id, event.member.id)]
    }
}

