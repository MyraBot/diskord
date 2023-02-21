package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.cache.MemberCacheKey
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.entities.guild.MemberData
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.MemberJoinEvent
import bot.myra.diskord.gateway.events.impl.guild.MemberRemoveEvent
import bot.myra.diskord.gateway.events.impl.guild.MemberUpdateEvent
import bot.myra.diskord.gateway.events.types.Event

class MutableMemberCachePolicy : MemberCachePolicy() {

    private fun checkIntents(event: Event) {
        if (GatewayIntent.GUILD_MEMBERS !in event.diskord.intents) {
            throw MissingIntentException(MemberCachePolicy::class, GatewayIntent.GUILD_MEMBERS)
        }
    }

    @ListenTo(MemberJoinEvent::class)
    suspend fun onMemberJoin(event: MemberJoinEvent) {
        checkIntents(event)
        update(event.member.data)
    }
    @ListenTo(MemberUpdateEvent::class)
    suspend fun onMemberUpdate(event: MemberUpdateEvent) {
        checkIntents(event)
        update(event.member.data)
    }

    @ListenTo(MemberRemoveEvent::class)
    suspend fun onMemberRemove(event: MemberRemoveEvent) {
        checkIntents(event)
        remove(MemberCacheKey(event.guildId, event.user.id))
    }

}

class DisabledMemberCachePolicy : MemberCachePolicy()

abstract class MemberCachePolicy : GenericCachePolicy<MemberCacheKey, MemberData>() {
    override fun getAsKey(value: MemberData): MemberCacheKey = MemberCacheKey(value.guildId, value.user.id)
}