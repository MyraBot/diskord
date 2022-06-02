package bot.myra.diskord.gateway

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = OpCode.Serializer::class)
enum class OpCode(val code: Int) {
    DISPATCH(0),
    HEARTBEAT(1),
    IDENTIFY(2),
    PRESENCE_UPDATE(3),
    VOICE_STATE_UPDATE(4),
    RESUME(6),
    RECONNECT(7),
    REQUEST_GUILD_MEMBERS(8),
    INVALID_SESSION(9),
    HELLO(10),
    HEARTBEAT_ACKNOWLEDGE(11),
    INVALID(-1);

    internal object Serializer : KSerializer<OpCode> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("gateway_operations", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder): OpCode = decoder.decodeInt().let { int -> values().first { it.code == int } }
        override fun serialize(encoder: Encoder, value: OpCode) = encoder.encodeInt(value.code)
    }

    companion object {
        fun from(code: Int): OpCode = values().first { it.code == code }
    }

}