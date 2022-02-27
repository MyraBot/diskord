package com.github.myraBot.diskord.gateway.events.impl.guild.voice

import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred

data class VoiceJoinEvent(val voiceState: VoiceState) : Event() {
    fun getChannelAsync(): Deferred<VoiceChannel?> = voiceState.getChannelAsync()
}
