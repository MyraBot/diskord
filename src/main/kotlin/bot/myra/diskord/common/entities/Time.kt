package bot.myra.diskord.common.entities

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

@Serializable(with = Time.TimeSerializer::class)
class Time(val instant: Instant) {
    val millis: Long get() = instant.toEpochMilli()

    internal object TimeSerializer : KSerializer<Time> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("time", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Time) {
            encoder.encodeLong(value.millis)
        }

        override fun deserialize(decoder: Decoder): Time {
            return Time(Instant.parse(decoder.decodeString()))
        }
    }


}