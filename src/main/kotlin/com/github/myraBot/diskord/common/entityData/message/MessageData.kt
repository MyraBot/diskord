package com.github.myraBot.diskord.common.entityData.message

import com.github.m5rian.discord.objects.entities.UserData
import com.github.myraBot.diskord.common.entityData.MemberData
import com.github.myraBot.diskord.common.entityData.channel.ChannelData
import com.github.myraBot.diskord.utilities.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#message-object)
 */
@Serializable
data class MessageData(
        val id: String,
        @SerialName("channel_id") val channelId: String,
        @SerialName("guild_id") val guildId: String? = null,
        @SerialName("author") val user: UserData,
        val member: MemberData? = null,
        val content: String,
        @Serializable(with = InstantSerializer::class) val timestamp: Instant,
        @Serializable(with = InstantSerializer::class) val edited: Instant?,
        val tts: Boolean,
        @SerialName("mention_everyone") val mentionsEveryone: Boolean = false,
        @SerialName("mentions") val mentionedUsers: List<UserData>,
        @SerialName("mention_roles") val mentionedRoles: List<String>,
        @SerialName("mention_channels") val mentionedChannels: List<ChannelData> = emptyList(),
        val pinned: Boolean,
        @SerialName("webhook_id") internal val webhookId: String? = null,
        val type: MessageType,
        val flags: MessageFlags = MessageFlags(0)
)