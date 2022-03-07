package bot.myra.diskord.common.entities.message

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.User
import bot.myra.diskord.common.entities.applicationCommands.components.Component
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.MemberData
import bot.myra.diskord.common.entities.message.embed.Embed
import bot.myra.diskord.common.serializers.SInstant
import bot.myra.diskord.rest.JumpUrlEndpoints
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.behaviors.MessageBehavior
import bot.myra.diskord.rest.behaviors.getChannelAsync
import bot.myra.diskord.rest.modifiers.message.components.MessageModifier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
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
    val pinned: Boolean,
    @SerialName("webhook_id") internal val webhookId: String? = null,
    val type: MessageType,
    val flags: MessageFlags = MessageFlags(),
    val components: MutableList<Component> = mutableListOf(),
) : MessageBehavior {
    override val message: Message = this
    val link: String get() = JumpUrlEndpoints.get(guildId.value!!, channelId, id)
    val isWebhook: Boolean = webhookId != null
    val isSystem: Boolean = flags.contains(MessageFlag.URGENT)

    fun getGuildAsync(): Deferred<Guild?> = guildId.value?.let { Diskord.getGuildAsync(it) } ?: CompletableDeferred(null)
    fun getChannelAsync(): Deferred<ChannelData?> = Diskord.getChannelAsync(channelId)
    inline fun <reified T> getChannelAsAsync(): Deferred<T?> = Diskord.getChannelAsync<T>(channelId)

    fun getMemberAsync(): Deferred<Member> {
        return if (!guildId.missing && memberData != null) {
            CompletableDeferred(Member.withUser(memberData, guildId.value!!, user))
        } else CompletableDeferred(null)
    }

    fun asBuilder(): MessageModifier =
        MessageModifier().apply {
            content = this@Message.content
            tts = this@Message.tts
            embeds = this@Message.embeds
            components = this@Message.components
        }
}