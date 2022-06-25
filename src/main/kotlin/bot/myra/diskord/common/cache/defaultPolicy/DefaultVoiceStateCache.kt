package bot.myra.diskord.common.cache.defaultPolicy

import bot.myra.diskord.common.cache.DoubleKey
import bot.myra.diskord.common.cache.models.VoiceStateCachePolicy
import bot.myra.diskord.common.entities.guild.voice.VoiceState

class DefaultVoiceStateCache {

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