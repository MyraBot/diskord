package bot.myra.diskord.common.entities.message

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.components.Component
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.MemberData
import bot.myra.diskord.common.entities.message.embed.Embed
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.common.serializers.SInstant
import bot.myra.diskord.common.utilities.MessageLink
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.behaviors.MessageBehavior
import bot.myra.diskord.rest.behaviors.getChannel
import bot.myra.diskord.rest.modifiers.message.components.MessageModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#message-object)
 */
@Suppress("unused")
@Serializable
data class Message(
    override val id: String,
    @SerialName("channel_id") val channelId: String,
    @SerialName("guild_id") internal val guildId: Optional<String> = Optional.Missing(),
    @SerialName("author") val user: User,
    @SerialName("member") internal val memberData: MemberData? = null,
    val content: String,
    @Serializable(with = SInstant::class) val timestamp: Instant,
    @Serializable(with = SInstant::class) val edited: Instant?,
    val tts: Boolean,
    @SerialName("mention_everyone") val mentionsEveryone: Boolean = false,
    @SerialName("mentions") val mentionedUsers: List<User>,
    @SerialName("mention_roles") val mentionedRoles: List<String>,
    @SerialName("mention_channels") val mentionedChannels: List<ChannelData> = emptyList(),
    val attachments: List<Attachment>,
    var embeds: MutableList<Embed>,
    val reactions: List<Reaction> = emptyList(),
    val pinned: Boolean,
    @SerialName("webhook_id") internal val webhookId: String? = null,
    val type: MessageType,
    val flags: MessageFlags = MessageFlags(),
    val components: MutableList<Component> = mutableListOf(),
) : MessageBehavior {
    override val message: Message = this
    val isWebhook: Boolean = webhookId != null
    val isSystem: Boolean = flags.contains(MessageFlag.URGENT)

    suspend fun isFromGuild(): Boolean = guildId.value !== null || Diskord.cachePolicy.channel.getGuild(channelId) != null
    suspend fun getLink(): String = Diskord.cachePolicy.channel.getGuild(channelId)?.let { MessageLink.guild(it, channelId, id) } ?: MessageLink.dms(channelId, id)
    suspend fun getGuild() = guildId.value?.let { Diskord.getGuild(it) }
    suspend fun getMember(): Member? {
        return if (guildId.missing) null
        else if (memberData != null) Member.withUser(memberData, guildId.value!!, user)
        else Diskord.getMember(guildId.value!!, id)
    }

    suspend fun getChannel(): ChannelData? = Diskord.getChannel(channelId)
    suspend inline fun <reified T> getChannelAs(): T? = Diskord.getChannel<T>(channelId)

    fun asModifier(): MessageModifier = MessageModifier().apply {
        content = this@Message.content
        tts = this@Message.tts
        embeds = this@Message.embeds
        components = this@Message.components
        attachments = this@Message.attachments.toMutableList()
    }
}