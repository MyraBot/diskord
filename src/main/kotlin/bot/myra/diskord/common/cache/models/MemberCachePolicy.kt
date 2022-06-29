package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.DoubleKey
import bot.myra.diskord.common.cache.GenericCachePolicy
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
}

class DisabledMemberCachePolicy : MemberCachePolicy()

abstract class MemberCachePolicy : GenericCachePolicy<DoubleKey<String, String>, Member>() {

    @ListenTo(MemberJoinEvent::class)
    fun onMemberJoin(event: MemberJoinEvent) = update(event.member)

    @ListenTo(MemberUpdateEvent::class)
    fun onMemberUpdate(event: MemberUpdateEvent) = update(event.member)

    @ListenTo(MemberRemoveEvent::class)
    fun onMemberRemove(event: MemberRemoveEvent) = remove(DoubleKey(event.removedMember.guildId, event.removedMember.user.id))

}

