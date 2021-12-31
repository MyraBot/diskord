package diskord.common.entities.message

import diskord.common.caching.ChannelCache
import diskord.common.caching.GuildCache
import diskord.common.caching.MemberCache
import diskord.common.entities.Channel
import diskord.common.entities.User
import diskord.common.entities.channel.TextChannel
import diskord.common.entities.guild.Guild
import diskord.common.entities.guild.Member
import diskord.common.entities.guild.MemberData
import diskord.common.entities.applicationCommands.components.Component
import diskord.common.entities.message.embed.Embed
import diskord.common.entityData.message.MessageFlag
import diskord.common.entityData.message.MessageFlags
import diskord.common.entityData.message.MessageType
import diskord.rest.JumpUrlEndpoints
import diskord.rest.behaviors.MessageBehavior
import diskord.rest.builders.MessageBuilder
import diskord.utilities.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#message-object)
 */
@Serializable
data class Message(
        override val id: String,
        @SerialName("channel_id") val channelId: String,
        @SerialName("guild_id") internal val guildId: String? = null,
        @SerialName("author") val user: User,
        @SerialName("member") internal val memberData: MemberData? = null,
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
    override val message: Message = this
    val link: String get() = JumpUrlEndpoints.get(ChannelCache[channelId]!!.guildId!!, channelId, id)
    val guild: Guild? get() = guildId?.let { GuildCache[it] } ?: channel.guild
    val isWebhook: Boolean = webhookId != null
    val isSystem: Boolean = flags.contains(MessageFlag.URGENT)
    val channel: TextChannel get() = ChannelCache.getAs<TextChannel>(channelId)!!

    fun asBuilder(): MessageBuilder =
        MessageBuilder().apply {
            content = this@Message.content
            tts = this@Message.tts
            embeds = this@Message.embeds
            actionRows = this@Message.components
        }
}

val Message.member: Member?
    get() = guildId?.let { g ->
        memberData?.let { m ->
            Member.withUser(m, g, user)
        } ?: MemberCache[g, user.id]
    } ?: channel.guild?.let { g ->
        memberData?.let { m ->
            Member.withUser(m, g.id, user)
        } ?: MemberCache[g.id, user.id]
    }