package bot.myra.diskord.common.entities.guild.voice

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Time
import bot.myra.diskord.common.entities.channel.VoiceChannel
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.MemberData
import bot.myra.diskord.rest.behaviors.getChannelAsync
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
data class VoiceState(
    @SerialName("guild_id") internal var guildId: String? = null,
    @SerialName("channel_id") val channelId: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("member") private val memberData: MemberData? = null,
    @SerialName("session_id") val sessionId: String,
    @SerialName("deaf") val isGuildDeaf: Boolean,
    @SerialName("mute") val isGuildMuted: Boolean,
    @SerialName("self_deaf") val isSelfDeaf: Boolean,
    @SerialName("self_mute") val isSelfMute: Boolean,
    @SerialName("self_stream") val isStreaming: Boolean? = null,
    @SerialName("self_video") val hasVideo: Boolean,
    @SerialName("request_to_speak_timestamp") val requestToSpeak: Time? = null,
) {
    val isMuted: Boolean = isSelfMute || isGuildMuted
    val isDeaf: Boolean = isSelfDeaf || isGuildDeaf

    fun getMemberAsync(): Deferred<Member> {
        return guildId?.let { it ->
            CompletableDeferred(Member.withUserInMember(memberData!!, it))
        } ?: CompletableDeferred(null)
    }

    fun getChannelAsync(): Deferred<VoiceChannel?> = channelId?.let { Diskord.getChannelAsync(it) } ?: CompletableDeferred(null)
    val channel: VoiceChannel? get() = runBlocking { getChannelAsync().await() }
}