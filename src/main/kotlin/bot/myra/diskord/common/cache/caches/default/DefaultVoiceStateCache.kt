package bot.myra.diskord.common.cache.caches.default

import bot.myra.diskord.common.cache.VoiceStateCacheKey
import bot.myra.diskord.common.cache.models.MutableVoiceStateCachePolicy
import bot.myra.diskord.common.cache.models.VoiceStateCachePolicy
import bot.myra.diskord.common.entities.guild.voice.VoiceState

class DefaultVoiceStateCache {

    private val map = mutableMapOf<VoiceStateCacheKey, VoiceState>()

    fun policy(): VoiceStateCachePolicy = MutableVoiceStateCachePolicy().apply {
        view {
            try {
                map.values.toList()
            } catch (e: Exception) {
                println("VOICE STATE CACHE! ERROR")
                println("values:")
                map.forEach { (k, v) ->
                    println("$k - $v")
                }
                e.printStackTrace()
                emptyList()
            }
        }
        get { map[it] }
        update { map[getAsKey(it)] = it }
        remove { map.remove(VoiceStateCacheKey(it.id.guild, it.id.user)) }
    }

}