package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.ListenTo
import com.github.myraBot.diskord.gateway.listeners.impl.guild.GuildCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.voice.VoiceStateUpdateEvent


object VoiceStateCache : Cache<String, MutableList<VoiceState>>() {

    override fun retrieve(key: String): MutableList<VoiceState> {
        return mutableListOf()
    }

    override fun update(value: MutableList<VoiceState>) {
        value.forEach { updateVoiceState(it) }
    }

    fun getMember(guildId: String, userId: String): VoiceState? = this.map
        .flatMap { it.value }
        .firstOrNull { it.guildId == guildId && it.userId == userId }

    private fun updateVoiceState(voiceState: VoiceState) {
        // Add voice state
        voiceState.channelId?.let { channelId ->
            map.values.forEach { voiceStates ->
                voiceStates.removeIf { it.userId == voiceState.userId } // Remove old voice states ➜ important if user got moved to a different channel
            }

            val list = this.map.getOrPut(channelId) { mutableListOf() } // Create new map entry if the current channel doesn't exist in the map yet
            list.add(voiceState) // Add new voice state
        } ?:
        // Remove voice state
        run {
            val map = mutableMapOf<String, MutableList<VoiceState>>()
            this.map.forEach { entry ->
                map[entry.key] = entry.value.filter { it.userId != voiceState.userId }.toMutableList()
            }
            this.map.clear()
            this.map.putAll(map)
        }

        this.map.entries.removeIf { it.value.isEmpty() } // If nobody is connected to the channel, remove channel from cache entirely
    }

    @ListenTo(GuildCreateEvent::class)
    fun onGuildCreate(event: GuildCreateEvent) = update(event.guild.voiceStates.toMutableList())


    @ListenTo(VoiceStateUpdateEvent::class)
    fun onVoiceStateUpdate(event: VoiceStateUpdateEvent) = update(mutableListOf(event.newVoiceState))

}