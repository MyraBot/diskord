package com.github.myraBot.diskord.gateway.listeners.impl.guild.voice

import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.request.Promise

data class VoiceLeaveEvent(
        private val newVoiceState: VoiceState,
        private val oldVoiceState: VoiceState
) : Event() {
    val member: Promise<Member> get() = newVoiceState.member
    val channel: Promise<VoiceChannel> get() = oldVoiceState.channel
}