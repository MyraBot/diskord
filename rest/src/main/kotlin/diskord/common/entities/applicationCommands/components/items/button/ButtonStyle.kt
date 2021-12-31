package diskord.common.entities.applicationCommands.components.items.button

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#button-object-button-styles)
 *
 * @property value Discords given code to the button style.
 */
@Serializable(with = ButtonStyle.Serializer::class)
enum class ButtonStyle(val value: Int) {
    PRIMARY(1),
    SECONDARY(2),
    SUCCESS(3),
    DANGER(4),
    LINK(5);

    internal object Serializer : KSerializer<ButtonStyle> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ButtonStyle", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: ButtonStyle) = encoder.encodeInt(value.value)
        override fun deserialize(decoder: Decoder): ButtonStyle = decoder.decodeInt().let {
            values().first { style -> style.value == it }
        }
    }
}