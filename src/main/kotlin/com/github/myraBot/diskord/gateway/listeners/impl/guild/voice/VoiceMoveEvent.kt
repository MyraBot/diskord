package com.github.myraBot.diskord.gateway.listeners.impl.guild.voice

import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.Event

data class VoiceMoveEvent(
        val oldVoiceState: VoiceState,
        val newVoiceState: VoiceState
) : Event()