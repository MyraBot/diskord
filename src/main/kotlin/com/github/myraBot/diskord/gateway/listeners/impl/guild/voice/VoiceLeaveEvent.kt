package com.github.myraBot.diskord.gateway.listeners.impl.guild.voice

import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.Event

data class VoiceLeaveEvent(
        val voiceState: VoiceState,
) : Event() {
    val channel: VoiceChannel get() = voiceState.channel!!
}