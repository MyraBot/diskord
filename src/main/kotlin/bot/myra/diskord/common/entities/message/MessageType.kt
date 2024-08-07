package bot.myra.diskord.common.entities.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#message-object-message-types)
 */
@Serializable(with = MessageType.Serializer::class)
enum class MessageType(val code: Int) {
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
    CONTEXT_MENU_COMMAND(23),
    AUTO_MODERATION_ACTION(24),
    ROLE_SUBSCRIPTION_PURCHASE(25),
    INTERACTION_PREMIUM_UPSELL(26),
    STAGE_START(27),
    STAGE_END(28),
    STAGE_SPEAKER(29),
    STAGE_TOPIC(31),
    GUILD_APPLICATION_PREMIUM_SUBSCRIPTION(32);

    internal object Serializer : KSerializer<MessageType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MessageType", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: MessageType) = encoder.encodeInt(value.code)
        override fun deserialize(decoder: Decoder): MessageType = decoder.decodeInt().let {
            entries.firstOrNull { type -> type.code == it } ?: throw IllegalStateException("Unknown MessageType: $it")
        }
    }
}