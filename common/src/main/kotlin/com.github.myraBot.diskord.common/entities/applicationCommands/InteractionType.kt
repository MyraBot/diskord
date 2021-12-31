package com.github.myraBot.diskord.common.entities.applicationCommands

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-data-structure)
 *
 * @property value Identify number.
 */
@Serializable(with = InteractionType.Serializer::class)
enum class InteractionType(val value: Int) {

    PING(1),
    APPLICATION_COMMAND(2),
    MESSAGE_COMPONENT(3),
    APPLICATION_COMMAND_AUTOCOMPLETE(4);

    internal object Serializer : KSerializer<InteractionType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("InteractionType", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: InteractionType) = encoder.encodeInt(value.value)
        override fun deserialize(decoder: Decoder): InteractionType = decoder.decodeInt().let { type -> values().first { it.value == type } }
    }
}