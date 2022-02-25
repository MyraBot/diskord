package com.github.myraBot.diskord.gateway.events.impl.guild.voice

import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.events.Event

data class VoiceUndeafEvent(
        val newVoiceState: VoiceState
) : Event()