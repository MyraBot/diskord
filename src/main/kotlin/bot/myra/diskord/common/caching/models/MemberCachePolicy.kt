package bot.myra.diskord.common.caching.models

import bot.myra.diskord.common.caching.DoubleKey
import bot.myra.diskord.common.caching.GenericCachePolicy
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.MemberUpdateEvent

class MemberCachePolicy : GenericCachePolicy<DoubleKey<String, String>, Member>() {

    @ListenTo(MemberUpdateEvent::class)
    fun onMemberUpdate(event: MemberUpdateEvent) = update(event.member)

}

