package bot.myra.diskord.common.cache.defaultPolicy

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.DoubleKey
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.cache.models.VoiceStateCachePolicy
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.gateway.GatewayIntent

class DefaultVoiceStateCache {

    init {
        if (GatewayIntent.GUILD_VOICE_STATES !in Diskord.intents && Diskord.cachePolicy.voiceState.active   ) {
            throw MissingIntentException(Diskord.cachePolicy.voiceState, GatewayIntent.GUILD_VOICE_STATES)
        }
    }

    private val map = mutableMapOf<DoubleKey<String?, String>, VoiceState>()

    fun policy(): VoiceStateCachePolicy = VoiceStateCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update {
            val key = DoubleKey(it.guildId, it.userId)
            map[key] = it
        }
        remove { map.remove(it) }
    }

}