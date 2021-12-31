package diskord.gateway.listeners.impl.guild.voice

import diskord.Diskord
import diskord.common.entities.channel.VoiceChannel
import diskord.common.entities.guild.Guild
import diskord.common.entities.guild.Member
import diskord.common.entities.guild.voice.VoiceState
import diskord.gateway.listeners.Event
import diskord.rest.behaviors.getChannel
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
