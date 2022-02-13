package com.github.myraBot.diskord.common.entities.message

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.Optional
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.applicationCommands.components.Component
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.message.embed.Embed
import com.github.myraBot.diskord.common.isMissing
import com.github.myraBot.diskord.rest.JumpUrlEndpoints
import com.github.myraBot.diskord.rest.behaviors.MessageBehavior
import com.github.myraBot.diskord.rest.behaviors.getChannel
import com.github.myraBot.diskord.rest.builders.MessageBuilder
import com.github.myraBot.diskord.rest.request.promises.Promise
import com.github.myraBot.diskord.utilities.InstantSerializer
import diskord.common.entityData.message.MessageFlag
import diskord.common.entityData.message.MessageFlags
import diskord.common.entityData.message.MessageType
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
    @SerialName("guild_id") internal val guildId: Optional<String> = Optional.Missing(),
    @SerialName("author") val user: User,
    @SerialName("member") internal val memberData: MemberData? = null,
    val content: String,
    @Serializable(with = InstantSerializer::class) val timestamp: Instant,
    @Serializable(with = InstantSerializer::class) val edited: Instant?,
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
    val flags: MessageFlags = MessageFlags(0),
    val components: MutableList<Component> = mutableListOf(),
) : MessageBehavior {
    override val message: Message = this
    val link: String get() = JumpUrlEndpoints.get(guildId.forceValue, channelId, id)
    val isWebhook: Boolean = webhookId != null
    val isSystem: Boolean = flags.contains(MessageFlag.URGENT)

    suspend fun getGuild(): Promise<Guild> = guildId.value?.let { Diskord.getGuild(it) } ?: Promise.of(null)
    suspend fun getChannel(): Promise<ChannelData> = Diskord.getChannel(channelId)
    suspend inline fun <reified T> getChannelAs(): Promise<T> = Diskord.getChannel<T>(channelId)
    val member: Promise<Member>
        get() {
            return if (!guildId.isMissing() && memberData != null) {
                Promise.of(Member.withUser(memberData, guildId.forceValue, user))
            } else {
                return if (memberData != null) Promise.of(Member.withUser(memberData, guildId.forceValue, user))
                else Promise.of(null)


                /*
                val guild = getChannel().then { it!!.getGuild() }
                return if (memberData != null) {
                    guild.then { Promise.of(Member.withUser(memberData, it!!.id, user)) }
                } else {
                    guild.then { MemberCache[DoubleKey(user.id, it!!.id)] }
                }

                 */
            }
        }

    fun asBuilder(): MessageBuilder =
        MessageBuilder().apply {
            content = this@Message.content
            tts = this@Message.tts
            embeds = this@Message.embeds
            actionRows = this@Message.components
        }
}