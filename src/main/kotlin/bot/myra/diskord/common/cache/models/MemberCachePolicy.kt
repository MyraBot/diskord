package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.cache.MemberCacheKey
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.MemberJoinEvent
import bot.myra.diskord.gateway.events.impl.guild.MemberRemoveEvent
import bot.myra.diskord.gateway.events.impl.guild.MemberUpdateEvent

class MutableMemberCachePolicy : MemberCachePolicy() {

    init {
        if (GatewayIntent.GUILD_MEMBERS !in Diskord.intents) throw MissingIntentException(MemberCachePolicy::class, GatewayIntent.GUILD_MEMBERS)
    }

    @ListenTo(MemberJoinEvent::class)
    suspend fun onMemberJoin(event: MemberJoinEvent) = update(event.member)

    @ListenTo(MemberUpdateEvent::class)
    suspend fun onMemberUpdate(event: MemberUpdateEvent) = update(event.member)

    @ListenTo(MemberRemoveEvent::class)
    suspend fun onMemberRemove(event: MemberRemoveEvent) = remove(MemberCacheKey(event.removedMember.guildId, event.removedMember.user.id))

}

class DisabledMemberCachePolicy : MemberCachePolicy()

abstract class MemberCachePolicy : GenericCachePolicy<MemberCacheKey, Member>() {
    override fun getAsKey(value: Member): MemberCacheKey = MemberCacheKey(value.guildId, value.id)
}