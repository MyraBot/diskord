package com.github.myraBot.diskord.gateway.listeners.impl.guild.voice

import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.request.Promise

data class VoiceJoinEvent(val voiceState: VoiceState) : Event() {
    val channel: Promise<VoiceChannel> get() = voiceState.channel
}
