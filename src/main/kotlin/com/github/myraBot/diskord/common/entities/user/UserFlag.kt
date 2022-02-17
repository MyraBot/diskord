package com.github.myraBot.diskord.common.entities.user

import com.github.myraBot.diskord.common.Optional
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class UserFlag(val code: Int) {
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

        }

        override fun deserialize(decoder: Decoder): Optional<List<UserFlag>> {
            val bitCode = decoder.decodeInt()
            return if (bitCode == 0) {
                decoder.decodeNull()
                Optional.Missing()
            } else {
                val flags = values().filter { bitCode == bitCode or it.code }
                Optional(flags.toList())
            }
        }


    }

}