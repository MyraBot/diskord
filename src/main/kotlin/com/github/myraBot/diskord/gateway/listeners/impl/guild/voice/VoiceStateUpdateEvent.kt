package com.github.myraBot.diskord.gateway.listeners.impl.guild.voice

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.caching.VoiceCache
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.request.promises.Promise
import kotlinx.serialization.SerialName

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#voice-state-update)
 *
 * Sent when someone joins/leaves/moves voice channels.
 *
 * @property newVoiceState Information about the current voice state of the member.
 */
data class VoiceStateUpdateEvent(
        @SerialName("voice_state") val newVoiceState: VoiceState,
) : Event() {

    override suspend fun call() {
        if (oldVoiceState == null && newVoiceState.channelId != null) VoiceJoinEvent(newVoiceState).call()
        else if (oldVoiceState?.channelId != null && newVoiceState.channelId == null) VoiceLeaveEvent(newVoiceState, oldVoiceState).call()
        else if (oldVoiceState != null && oldVoiceState.channelId != newVoiceState.channelId) VoiceMoveEvent(oldVoiceState, newVoiceState).call()

        if (oldVoiceState?.isMuted == false && newVoiceState.isMuted) VoiceMuteEvent(newVoiceState).call()
        if (oldVoiceState?.isMuted == true && !newVoiceState.isMuted) VoiceUnmuteEvent(newVoiceState).call()

        if (oldVoiceState?.isDeaf == false && newVoiceState.isDeaf) VoiceDeafEvent(newVoiceState).call()
        if (oldVoiceState?.isDeaf == true && !newVoiceState.isDeaf) VoiceDeafEvent(newVoiceState).call()

        super.call()
    }

    val member: Promise<Member> get() = newVoiceState.member
    fun getGuild(): Promise<Guild> = newVoiceState.guildId?.let { Diskord.getGuild(it) } ?: Promise.of(null)
    val oldVoiceState: VoiceState? = VoiceCache.collect().flatten().firstOrNull { it.userId == newVoiceState.userId && it.guildId == it.guildId }

}
