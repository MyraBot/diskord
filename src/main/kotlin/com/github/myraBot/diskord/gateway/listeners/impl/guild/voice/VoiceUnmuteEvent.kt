package com.github.myraBot.diskord.gateway.listeners.impl.guild.voice

import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.Event

data class VoiceUnmuteEvent(
        val newVoiceState: VoiceState
) : Event()