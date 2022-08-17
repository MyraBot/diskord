package bot.myra.diskord.common.cache.defaultPolicy

import bot.myra.diskord.common.cache.DoubleKey
import bot.myra.diskord.common.cache.models.MutableVoiceStateCachePolicy
import bot.myra.diskord.common.cache.models.VoiceStateCachePolicy
import bot.myra.diskord.common.entities.guild.voice.VoiceState

class DefaultVoiceStateCache {

    private val map = mutableMapOf<DoubleKey<String?, String>, VoiceState>()

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
        update {
            val key = DoubleKey(it.guildId, it.userId)
            map[key] = it
        }
        remove { map.remove(it) }
    }

}