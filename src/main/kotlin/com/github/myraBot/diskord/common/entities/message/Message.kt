package com.github.myraBot.diskord.common.entities.message

import com.github.myraBot.diskord.common.entities.Channel
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.channel.MessageChannel
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.guild.SimpleGuild
import com.github.myraBot.diskord.common.entities.interaction.components.Component
import com.github.myraBot.diskord.common.entities.message.embed.Embed
import com.github.myraBot.diskord.common.entityData.message.MessageFlag
import com.github.myraBot.diskord.common.entityData.message.MessageFlags
import com.github.myraBot.diskord.common.entityData.message.MessageType
import com.github.myraBot.diskord.rest.JumpUrlEndpoints
import com.github.myraBot.diskord.rest.behaviors.MessageBehavior
import com.github.myraBot.diskord.utilities.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#message-object)
 */
@Serializable
data class Message(
        val id: String,
        @SerialName("channel_id") val channelId: String,
        @SerialName("guild_id") private val guildId: String? = null,
        @SerialName("author") val user: User,
        @SerialName("member") val memberData: MemberData? = null,
        val content: String,
        @Serializable(with = InstantSerializer::class) val timestamp: Instant,
        @Serializable(with = InstantSerializer::class) val edited: Instant?,
        val tts: Boolean,
        @SerialName("mention_everyone") val mentionsEveryone: Boolean = false,
        @SerialName("mentions") val mentionedUsers: List<User>,
        @SerialName("mention_roles") val mentionedRoles: List<String>,
        @SerialName("mention_channels") val mentionedChannels: List<Channel> = emptyList(),
        val attachments: List<Attachment>,
        var embeds: MutableList<Embed>,
        val pinned: Boolean,
        @SerialName("webhook_id") internal val webhookId: String? = null,
        val type: MessageType,
        val flags: MessageFlags = MessageFlags(0),
        val components: MutableList<Component> = mutableListOf()
) : MessageBehavior {
    val link: String = JumpUrlEndpoints.get(guildId!!, channelId, id)
    val guild: SimpleGuild? = guildId?.let { SimpleGuild(it) }
    val member: Member get() = Member.withUser(memberData!!, guild!!, user)
    val isWebhook: Boolean = webhookId == null
    val isSystem: Boolean = flags.contains(MessageFlag.URGENT)
    val channel: MessageChannel = MessageChannel(channelId)
}








