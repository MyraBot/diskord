package bot.myra.diskord.common.entities.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = MessageFlags.Serializer::class)
class MessageFlags(val code: Int) {
    operator fun contains(flag: MessageFlag) = flag.code and this.code == flag.code

    internal object Serializer : KSerializer<MessageFlags> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("message_flags", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder): MessageFlags = MessageFlags(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: MessageFlags) = encoder.encodeInt(value.code)
    }
}

object MessageFlagsSerializer : KSerializer<MutableList<MessageFlag>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("message_flag", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): MutableList<MessageFlag> {
        val flags = mutableListOf<MessageFlag>()
        decoder.decodeInt().let { bit ->
            flags.add(MessageFlag.values().first { bit and it.code == it.code })
        }
        return flags
    }

    override fun serialize(encoder: Encoder, value: MutableList<MessageFlag>) {
        val bitCode = value.map { 1 shl it.code }.reduce { acc, i -> acc or i }
        encoder.encodeInt(bitCode)
    }

}