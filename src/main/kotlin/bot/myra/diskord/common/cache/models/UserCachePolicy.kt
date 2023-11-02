package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.entities.user.UserData
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.MemberUpdateEvent

class MutableUserCachePolicy : UserCachePolicy() {

    @ListenTo(MemberUpdateEvent::class)
    suspend fun onGuildMemberUpdate(event:MemberUpdateEvent) {
        update(event.member.user.data)
    }

}

class DisabledUserCachePolicy : UserCachePolicy()

abstract class UserCachePolicy : GenericCachePolicy<String, UserData>() {
    override fun getAsKey(value: UserData): String = value.id
}