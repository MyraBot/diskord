package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.ListenTo
import com.github.myraBot.diskord.gateway.listeners.impl.guild.GuildCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.voice.VoiceStateUpdateEvent
import com.github.myraBot.diskord.rest.request.promises.Promise

object VoiceCache : Cache<String, MutableList<VoiceState>>(
    retrieve = { Promise.of(null) }
) {
    @ListenTo(GuildCreateEvent::class)
    fun onGuildCreate(event: GuildCreateEvent) = event.guild.voiceStates.forEach { updateVoiceState(it) }

    @ListenTo(VoiceStateUpdateEvent::class)
    fun onVoiceStateUpdate(event: VoiceStateUpdateEvent) = updateVoiceState(event.newVoiceState)

    private fun updateVoiceState(voiceState: VoiceState) {
        // Add voice state
        voiceState.channelId?.let { channelId ->
            cache.values.forEach { voiceStates ->
                voiceStates.removeIf { it.userId == voiceState.userId } // Remove old voice states ➜ important if user got moved to a different channel
            }

            val list = this.cache.getOrPut(channelId) { mutableListOf() } // Create new cache entry if the current channel doesn't exist in the cache yet
            list.add(voiceState) // Add new voice state
        } ?:
        // Remove voice state
        run {
            val cache = mutableMapOf<String, MutableList<VoiceState>>()
            this.cache.forEach { entry ->
                cache[entry.key] = entry.value.filter { it.userId != voiceState.userId }.toMutableList()
            }
            this.cache.clear()
            this.cache.putAll(cache)
        }

        this.cache.entries.removeIf { it.value.isEmpty() } // If nobody is connected to the channel, remove channel from cache entirely
    }

}