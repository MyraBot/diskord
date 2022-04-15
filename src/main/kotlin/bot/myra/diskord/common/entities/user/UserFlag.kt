package bot.myra.diskord.common.entities.user

import bot.myra.diskord.rest.Optional
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class UserFlag(val value: Int) {
    STAFF(1 shl 0),
    PARTNER(1 shl 1),
    HYPESQUAD(1 shl 2),
    BUG_HUNTER_LEVEL_1(1 shl 3),
    BRAVERY_MEMBER(1 shl 6),
    BRILLIANCE_MEMBER(1 shl 7),
    BALANCE_MEMBER(1 shl 8),
    PREMIUM_EARLY_SUPPORTER(1 shl 9),
    TEAM_PSEUDO_USER(1 shl 10),
    BUG_HUNTER_LEVEL_2(1 shl 14),
    VERIFIED_BOT(1 shl 16),
    VERIFIED_DEVELOPER(1 shl 17),
    CERTIFIED_MODERATOR(1 shl 18),
    BOT_HTTP_INTERACTIONS(1 shl 19);

    internal object Serializer : KSerializer<Optional<List<UserFlag>>> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Badge", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: Optional<List<UserFlag>>) {
            when (value.missing) {
                true  -> encoder.encodeInt(0)
                false -> {
                    if (value.value!!.isEmpty()) {
                        encoder.encodeInt(0)
                    } else {
                        val bitCode = value.value.map { it.value }.reduce { x, y -> x or y }
                        encoder.encodeInt(bitCode)
                    }
                }
            }
        }

        override fun deserialize(decoder: Decoder): Optional<List<UserFlag>> {
            val bitCode = decoder.decodeInt()
            return if (bitCode == 0) {
                decoder.decodeNull()
                Optional(emptyList())
            } else {
                val flags = values().filter { bitCode or it.value == bitCode }
                Optional(flags.toList())
            }
        }


    }

}