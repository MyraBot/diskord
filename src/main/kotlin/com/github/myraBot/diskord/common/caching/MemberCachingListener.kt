package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.gateway.listeners.ListenTo
import com.github.myraBot.diskord.gateway.listeners.impl.guild.MemberUpdateEvent

object MemberCachingListener : EventListener {
    @ListenTo(MemberUpdateEvent::class)
    fun onMemberUpdate(event: MemberUpdateEvent) = MemberCache.cache[DoubleKey(event.guild.id, event.member.id)]
}

