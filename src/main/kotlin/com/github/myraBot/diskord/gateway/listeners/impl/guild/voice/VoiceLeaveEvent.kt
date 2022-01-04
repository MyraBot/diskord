package com.github.myraBot.diskord.gateway.listeners.impl.guild.voice

import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.Event

data class VoiceLeaveEvent(
        private val newVoiceState: VoiceState,
        private val oldVoiceState: VoiceState
) : Event() {
    val member: Member? get() = newVoiceState.member
    val channel: VoiceChannel get() = oldVoiceState.channel!!
}