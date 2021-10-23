package com.github.myraBot.diskord.common.entities

import com.github.m5rian.discord.objects.entities.User
import com.github.myraBot.diskord.utilities.InstantSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#message-object)
 */
@Serializable
data class Message(
        val id: String,
        @SerialName("channel_id") internal val channelId: String,
        @SerialName("author") val user: User,
        val content: String,
        @Serializable(with = InstantSerializer::class) val timestamp: Instant,
        @Serializable(with = InstantSerializer::class) val edited: Instant?,
        val tts: Boolean,
        @SerialName("mention_everyone") val mentionsEveryone: Boolean = false,
        @SerialName("mentions") val mentionedUsers: List<User>,
        @SerialName("mention_roles") val mentionedRoles: List<String>,
        @SerialName("mention_channels") val mentionedChannels: List<TextChannel> = emptyList(),
        val pinned: Boolean,
        @SerialName("webhook_id") internal val webhookId: String? = null,
        @Serializable(with = MessageTypeSerializer::class) val type: Type
) {
    val isWebhook: Boolean get() = webhookId == null

    /**
     * [Documentation](https://discord.com/developers/docs/resources/channel#message-object-message-types)
     */
    @Serializable
    enum class Type(val code: Int) {
        DEFAULT(0),
        RECIPIENT_ADD(1),
        RECIPIENT_REMOVE(2),
        CALL(3),
        CHANNEL_NAME_CHANGE(4),
        CHANNEL_ICON_CHANGE(5),
        CHANNEL_PINNED_MESSAGE(6),
        GUILD_MEMBER_JOIN(7),
        USER_PREMIUM_GUILD_SUBSCRIPTION(8),
        USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_1(9),
        USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_2(10),
        USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_3(11),
        CHANNEL_FOLLOW_ADD(12),
        GUILD_DISCOVERY_DISQUALIFIED(14),
        GUILD_DISCOVERY_REQUALIFIED(15),
        GUILD_DISCOVERY_GRACE_PERIOD_INITIAL_WARNING(16),
        GUILD_DISCOVERY_GRACE_PERIOD_FINAL_WARNING(17),
        THREAD_CREATED(18),
        REPLY(19),
        CHAT_INPUT_COMMAND(20),
        THREAD_STARTER_MESSAGE(21),
        GUILD_INVITE_REMINDER(22),
        CONTEXT_MENU_COMMAND(23)
    }
}

class MessageTypeSerializer : KSerializer<Message.Type> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MessageType", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: Message.Type) = encoder.encodeInt(value.code)
    override fun deserialize(decoder: Decoder): Message.Type = Message.Type.values().first { it.code == decoder.decodeInt() }
}