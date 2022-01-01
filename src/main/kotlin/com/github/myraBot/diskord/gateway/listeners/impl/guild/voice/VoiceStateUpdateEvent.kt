package com.github.myraBot.diskord.gateway.listeners.impl.guild.voice

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.behaviors.getChannel
import kotlinx.serialization.SerialName
import java.time.Instant

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#voice-state-update)
 *
 * Sent when someone joins/leaves/moves voice channels.
 *
 * @property voiceState Information about the current voice state of the member.
 */
data class VoiceStateUpdateEvent(
        @SerialName("voice_state") private val voiceState: VoiceState
) : Event() {
    val member: Member? get() = voiceState.member?.let { Member.withUserInMember(it, voiceState.guildId!!) }
    val guild: Guild? get() = voiceState.guildId?.let { Diskord.getGuild(it) }
    val channel: VoiceChannel? get() = voiceState.channelId?.let { guild?.getChannel<VoiceChannel>(it) }

    val deaf: Boolean = voiceState.deaf
    val mute: Boolean = voiceState.mute
    val selfDeaf: Boolean = voiceState.selfDeaf
    val selfMute: Boolean = voiceState.selfMute
    val selfStream: Boolean? = voiceState.selfStream
    val selfVideo: Boolean = voiceState.selfVideo

    val requestToSpeak: Instant? = voiceState.requestToSpeak
}
