package bot.myra.diskord.rest

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@kotlinx.serialization.Serializable(with = Optional.MissingSerializer::class)
class Optional<T>(val value: T?) {
    var missing: Boolean = false

    companion object {
        fun <T> Missing(): Optional<T> = Optional<T>(null).apply { missing = true }
    }

    internal class MissingSerializer<T>(private val contentSerializer: KSerializer<T>) : KSerializer<Optional<T>> {
        override val descriptor: SerialDescriptor = contentSerializer.descriptor

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): Optional<T> {
            val optional: Optional<T?> = when {
                !decoder.decodeNotNullMark() -> {
                    decoder.decodeNull()
                    Missing()
                }
                else -> {
                    Optional(decoder.decodeSerializableValue(contentSerializer))
                }
            }

            @Suppress("UNCHECKED_CAST")
            return optional as Optional<T>
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: Optional<T>) {
            when {
                value.missing -> encoder.encodeNull()
                else -> encoder.encodeNullableSerializableValue(contentSerializer, value.value)
            }
        }
    }
}
