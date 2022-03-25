package bot.myra.diskord.common.caching.defaultPolicy

import bot.myra.diskord.common.caching.DoubleKey
import bot.myra.diskord.common.caching.models.VoiceStateCachePolicy
import bot.myra.diskord.common.entities.guild.voice.VoiceState

class DefaultVoiceStateCache {

    val map = mutableMapOf<DoubleKey<String?, String>, VoiceState>()

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