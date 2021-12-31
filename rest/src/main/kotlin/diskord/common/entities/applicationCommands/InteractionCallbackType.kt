package diskord.common.entities.applicationCommands

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = InteractionCallbackType.Serializer::class)
enum class InteractionCallbackType(val value: Int, val onlyComponents: Boolean) {
    CHANNEL_MESSAGE_WITH_SOURCE(4, false),
    DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE(5, false),
    DEFERRED_UPDATE_MESSAGE(6, true),
    UPDATE_MESSAGE(7, true),
    APPLICATION_COMMAND_AUTOCOMPLETE_RESULT(8, false);

    internal object Serializer : KSerializer<InteractionCallbackType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("InteractionCallbackType", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: InteractionCallbackType) = encoder.encodeInt(value.value)
        override fun deserialize(decoder: Decoder): InteractionCallbackType = decoder.decodeInt().let { type -> values().first { it.value == type } }
    }
}