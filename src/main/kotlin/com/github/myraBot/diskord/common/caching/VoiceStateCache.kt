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

    private fun updateVoiceState(voiceState: VoiceState) {
        voiceState.channelId?.let {
            println("Adding ${voiceState.userId}")
            this.map.getOrPut(it) { mutableListOf(voiceState) }.add(voiceState)
        } ?: this.map.forEach { entry ->
            println("Removing ${voiceState.userId}")
            this.map[entry.key] = entry.value.filter { it.userId != voiceState.userId }.toMutableList()
        }
    }

    @ListenTo(GuildCreateEvent::class)
    fun onGuildCreate(event: GuildCreateEvent) = update(event.guild.voiceStates.toMutableList()).also { println("yes") }

    @ListenTo(VoiceStateUpdateEvent::class)
    fun onVoiceStateUpdate(event: VoiceStateUpdateEvent) = update(mutableListOf(event.voiceState))

}