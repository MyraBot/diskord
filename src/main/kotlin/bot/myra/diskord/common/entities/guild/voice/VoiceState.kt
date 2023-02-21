package bot.myra.diskord.common.entities.guild.voice

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.PartialMemberData
import bot.myra.diskord.common.serializers.SInstant
import bot.myra.diskord.rest.behaviors.DiskordObject
import bot.myra.diskord.rest.behaviors.guild.VoiceStateBehavior
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Suppress("unused")
class VoiceState(
    override val data: VoiceStateData,
    override val diskord: Diskord
) : DiskordObject, VoiceStateBehavior {
    val isMuted get() = data.isSelfMute || data.isGuildMuted
    val isDeaf get() = data.isSelfDeaf || data.isGuildDeaf
    val member get() = data.guildId?.let { Member.fromPartialMember(data.memberData!!, it, diskord) }

    val guildId get() = data.guildId
    val channelId get() = data.channelId
    val userId get() = data.userId
    val memberData get() = data.memberData
    val sessionId get() = data.sessionId
    val isGuildDeaf get() = data.isGuildDeaf
    val isGuildMuted get() = data.isGuildMuted
    val isSelfDeaf get() = data.isSelfDeaf
    val isSelfMute get() = data.isSelfMute
    val isStreaming get() = data.isStreaming
    val hasVideo get() = data.hasVideo
    val requestToSpeak get() = data.requestToSpeak
}

/**
 * [Documentation](https://discord.com/developers/docs/resources/voice#voice-state-object)
 *
 * @property guildId Guild id of the voice state.
 * @property channelId The channel the user connected to.
 * @property userId The user id of the voice state.
 * @property memberData The member object of this voice state.
 * @property sessionId Voice state session id.
 * @property isGuildDeaf Whether the user got deafened.
 * @property isGuildMuted Whether the user got muted.
 * @property isSelfDeaf Whether the user has deafened himself.
 * @property isSelfMute Whether the user has muted himself.
 * @property isStreaming Whether the user is streaming.
 * @property hasVideo Whether the user enabled his camera.
 * @property requestToSpeak Time the user requested to speak. If null, the user isn't raising his hand.
 */
@Serializable
data class VoiceStateData(
    @SerialName("guild_id") internal var guildId: String? = null,
    @SerialName("channel_id") val channelId: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("member") val memberData: PartialMemberData? = null,
    @SerialName("session_id") val sessionId: String,
    @SerialName("deaf") val isGuildDeaf: Boolean,
    @SerialName("mute") val isGuildMuted: Boolean,
    @SerialName("self_deaf") val isSelfDeaf: Boolean,
    @SerialName("self_mute") val isSelfMute: Boolean,
    @SerialName("self_stream") val isStreaming: Boolean? = null,
    @SerialName("self_video") val hasVideo: Boolean,
    @Serializable(with = SInstant::class) @SerialName("request_to_speak_timestamp") val requestToSpeak: Instant? = null,
)