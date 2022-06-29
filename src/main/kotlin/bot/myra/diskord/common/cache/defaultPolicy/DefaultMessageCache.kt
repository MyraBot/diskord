package bot.myra.diskord.common.cache.defaultPolicy

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.cache.models.MessageCachePolicy
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.GatewayIntent

class DefaultMessageCache {

    init {
        if (GatewayIntent.GUILD_MESSAGES !in Diskord.intents && Diskord.cachePolicy.message.active) {
            throw MissingIntentException(Diskord.cachePolicy.message, GatewayIntent.GUILD_MESSAGES)
        }
    }

    private val map = mutableMapOf<String, Message>()

    fun policy(): MessageCachePolicy = MessageCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it) }
    }

}