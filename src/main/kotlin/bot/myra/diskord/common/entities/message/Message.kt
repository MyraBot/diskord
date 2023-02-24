package bot.myra.diskord.common.entities.message

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.components.Component
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.PartialMemberData
import bot.myra.diskord.common.entities.message.embed.Embed
import bot.myra.diskord.common.entities.user.UserData
import bot.myra.diskord.common.serializers.SInstant
import bot.myra.diskord.common.utilities.MessageLink
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.behaviors.MessageBehavior
import bot.myra.diskord.rest.getChannel
import bot.myra.diskord.rest.modifiers.message.components.MessageModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Suppress("unused")
class Message(
    override val data: MessageData,
    override val diskord: Diskord
) : MessageBehavior {
    override val id: String = data.id
    val channelId get() = data.channelId
    val guildId get() = data.guildId
    val user get() = data.user
    val memberData get() = data.memberData
    val content get() = data.content
    val timestamp get() = data.timestamp
    val edited get() = data.edited
    val tts get() = data.tts
    val mentionsEveryone get() = data.mentionsEveryone
    val mentionedUsers get() = data.mentionedUsers
    val mentionedRoles get() = data.mentionedRoles
    val mentionedChannels get() = data.mentionedChannels
    val attachments get() = data.attachments
    val embeds get() = data.embeds
    val reactions get() = data.reactions
    val pinned get() = data.pinned
    val webhookId get() = data.webhookId
    val type get() = data.type
    val flags get() = data.flags
    val components get() = data.components

    val modifier get() = data.modifier
    val isWebhook get() = data.webhookId != null
    val isSystem get() = data.flags.contains(MessageFlag.URGENT)

    suspend fun isFromGuild(): Boolean = data.guildId.value !== null || diskord.cachePolicy.channel.getGuild(channelId) != null
    suspend fun getLink(): String = diskord.cachePolicy.channel.getGuild(data.channelId)?.let { MessageLink.guild(it, data.channelId, id) } ?: MessageLink.dms(channelId, id)
    suspend fun getGuild() = data.guildId.value?.let { diskord.getGuild(it) }
    suspend fun getMember(): Member? {
        return if (guildId.missing) null
        else if (memberData != null) Member.fromPartialMember(memberData!!, guildId.value!!, user, diskord)
        else diskord.getMember(guildId.value!!, id).value
    }

    suspend fun getChannel() = diskord.getChannel(channelId)
    suspend inline fun <reified T> getChannelAs() = diskord.getChannel<T>(channelId)
}

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#message-object)
 */
@Suppress("unused")
@Serializable
data class MessageData(
    val id: String,
    @SerialName("channel_id") val channelId: String,
    @SerialName("guild_id") val guildId: Optional<String> = Optional.Missing(),
    @SerialName("author") val user: UserData,
    @SerialName("member") internal val memberData: PartialMemberData? = null,
    val content: String,
    @Serializable(with = SInstant::class) val timestamp: Instant,
    @Serializable(with = SInstant::class) val edited: Instant?,
    val tts: Boolean,
    @SerialName("mention_everyone") val mentionsEveryone: Boolean = false,
    @SerialName("mentions") val mentionedUsers: List<UserData>,
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
) {
    val modifier
        get() = MessageModifier().apply {
            content = this@MessageData.content
            tts = this@MessageData.tts
            embeds = this@MessageData.embeds
            components = this@MessageData.components
            attachments = this@MessageData.attachments.toMutableList()
        }
}