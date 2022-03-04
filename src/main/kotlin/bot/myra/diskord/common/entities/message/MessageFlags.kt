package bot.myra.diskord.common.entities.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = MessageFlags.MessageFlagsSerializer::class)
class MessageFlags(flags: MutableList<MessageFlag> = mutableListOf()) : MutableList<MessageFlag> by flags {

    internal object MessageFlagsSerializer : KSerializer<MessageFlags> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("message_flag", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): MessageFlags {
            val flags = mutableListOf<MessageFlag>()
            decoder.decodeInt().let { bit ->
                flags.add(MessageFlag.values().first { bit and it.code == it.code })
            }
            return MessageFlags(flags)
        }

        override fun serialize(encoder: Encoder, value: MessageFlags) {
            when (value.isEmpty()) {
                true -> encoder.encodeInt(0)
                false -> {
                    val bitCode = value.map { 1 shl it.code }.reduce { acc, i -> acc or i }
                    encoder.encodeInt(bitCode)
                }
            }
        }

    }

}