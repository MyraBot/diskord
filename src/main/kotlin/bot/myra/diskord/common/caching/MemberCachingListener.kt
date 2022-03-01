package bot.myra.diskord.common.caching

import bot.myra.diskord.gateway.events.EventListener
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.MemberUpdateEvent

object MemberCachingListener : EventListener {
    @ListenTo(MemberUpdateEvent::class)
    suspend fun onMemberUpdate(event: MemberUpdateEvent) {
        val guild = event.getGuildAsync().await()
        MemberCache.cache[DoubleKey(guild!!.id, event.member.id)]
    }
}

