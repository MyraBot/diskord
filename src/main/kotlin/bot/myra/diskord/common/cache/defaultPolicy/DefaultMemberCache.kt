package bot.myra.diskord.common.cache.defaultPolicy

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.DoubleKey
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.cache.models.MemberCachePolicy
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.gateway.GatewayIntent

class DefaultMemberCache {

    init {
        if (GatewayIntent.GUILD_MEMBERS !in Diskord.intents && Diskord.cachePolicy.member.active) {
            throw MissingIntentException(Diskord.cachePolicy.member, GatewayIntent.GUILD_MEMBERS)
        }
    }

    private val map = mutableMapOf<DoubleKey<String, String>, Member>()

    fun policy(): MemberCachePolicy = MemberCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update {
            val key = DoubleKey(it.guildId, it.id)
            map[key] = it
        }
        remove { map.remove(it) }
    }

}