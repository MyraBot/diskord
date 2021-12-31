package diskord.rest.builders

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#component-object-component-types)
 *
 * @property type
 */
@Serializable(with = ComponentType.Serializer::class)
enum class ComponentType(val type: Int) {
    ACTION_ROW(1),
    BUTTON(2),
    SELECT_MENU(3);

    internal object Serializer : KSerializer<ComponentType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ComponentType", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: ComponentType) = encoder.encodeInt(value.type)

        /**
         * [first] decodes the value on every iteration, that's why the second iteration will cause an error.
         * For this I store the decoded value in [let] to use it multiple times, without decoding again.
         *
         * @param decoder [Decoder]
         * @return Returns a [ComponentType] matching the type of [Decoder.decodeInt].
         */
        override fun deserialize(decoder: Decoder): ComponentType = decoder.decodeInt().let { type -> values().first { it.type == type } }
    }
}