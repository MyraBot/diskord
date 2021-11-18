package com.github.myraBot.diskord.common.entities.applicationCommands.slashCommands

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [Documentation](https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-type)
 *
 * @property value Discords option type index.
 */
@Serializable(with = SlashCommandOptionType.Serializer::class)
enum class SlashCommandOptionType(val value: Int) {

    SUB_COMMAND(1),
    SUB_COMMAND_GROUP(2),
    STRING(3),
    INTEGER(4),
    BOOLEAN(5),
    USER(6),
    CHANNEL(7),
    ROLE(8),
    MENTIONABLE(9),
    NUMBER(10);

    internal object Serializer : KSerializer<SlashCommandOptionType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SlashCommandOptionType", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: SlashCommandOptionType) = encoder.encodeInt(value.value)
        override fun deserialize(decoder: Decoder): SlashCommandOptionType = decoder.decodeInt().let { type -> values().first { it.value == type } }
    }

}