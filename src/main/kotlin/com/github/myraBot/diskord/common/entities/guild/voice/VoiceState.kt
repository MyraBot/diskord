package com.github.myraBot.diskord.common.entities.guild.voice

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.rest.behaviors.getChannel
import com.github.myraBot.diskord.utilities.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.Instant

/**
 * [Documentation](https://discord.com/developers/docs/resources/voice#voice-state-object)
 *
 * @property guildId Guild id of the voice state.
 * @property channelId The channel the user connected to.
 * @property userId The user id of the voice state.
 * @property member The member object of this voice state.
 * @property sessionId Voice state session id.
 * @property deaf Whether the user got deafened.
 * @property mute Whether the user got muted.
 * @property selfDeaf Whether the user has deafened himself.
 * @property selfMute Whether the user has muted himself.
 * @property selfStream Whether the user is streaming.
 * @property selfVideo Whether the user enabled his camera.
 * @property requestToSpeak Time the user requested to speak. If null, the user isn't raising his hand.
 */
@Serializable
data class VoiceState(
        @SerialName("guild_id") val guildId: String? = null,
        @SerialName("channel_id") val channelId: String? = null,
        @SerialName("user_id") val userId: String,
        @SerialName("member") private val memberData: MemberData? = null,
        @SerialName("session_id") val sessionId: String,
        val deaf: Boolean,
        val mute: Boolean,
        @SerialName("self_deaf") val selfDeaf: Boolean,
        @SerialName("self_mute") val selfMute: Boolean,
        @SerialName("self_stream") val selfStream: Boolean? = null,
        @SerialName("self_video") val selfVideo: Boolean,
        @Serializable(with = InstantSerializer::class) @SerialName("request_to_speak_timestamp") val requestToSpeak: Instant? = null
) {
    @Transient
    val member: Member? = memberData?.let { Member.withUserInMember(it, guildId!!) }
}