package com.github.myraBot.diskord.gateway.listeners.impl.guild.voice

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.caching.VoiceStateCache
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.serialization.SerialName

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#voice-state-update)
 *
 * Sent when someone joins/leaves/moves voice channels.
 *
 * @property newVoiceState Information about the current voice state of the member.
 */
data class VoiceStateUpdateEvent(
        @SerialName("voice_state") val newVoiceState: VoiceState
) : Event() {

    override suspend fun call() {
        if (oldVoiceState == null && newVoiceState.channel != null) VoiceLeaveEvent(newVoiceState).call()
        else if (oldVoiceState?.channel != null && newVoiceState.channelId == null) VoiceJoinEvent(newVoiceState).call()
        else if (oldVoiceState != null && oldVoiceState.channelId != newVoiceState.channelId) VoiceMoveEvent(oldVoiceState, newVoiceState).call()

        if (oldVoiceState?.isMuted == false && newVoiceState.isMuted) VoiceMuteEvent(newVoiceState).call()
        if (oldVoiceState?.isMuted == true && !newVoiceState.isMuted) VoiceUnmuteEvent(newVoiceState).call()

        if (oldVoiceState?.isDeaf == false && newVoiceState.isDeaf) VoiceDeafEvent(newVoiceState).call()
        if (oldVoiceState?.isDeaf == true && !newVoiceState.isDeaf) VoiceDeafEvent(newVoiceState).call()

        super.call()
    }

    val member: Member? get() = newVoiceState.member
    val guild: Guild? get() = newVoiceState.guildId?.let { Diskord.getGuild(it) }
    val oldVoiceState: VoiceState? = VoiceStateCache.map.values.flatten().firstOrNull { it.userId == newVoiceState.userId && it.guildId == it.guildId }

}
