package com.github.myraBot.diskord.gateway.events.impl.guild.voice

import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking

data class VoiceLeaveEvent(
    private val newVoiceState: VoiceState,
    private val oldVoiceState: VoiceState
) : Event() {
    fun getMemberAsync(): Deferred<Member> = newVoiceState.getMemberAsync()
    val channel: VoiceChannel get() = runBlocking { oldVoiceState.getChannelAsync().await()!! }
}