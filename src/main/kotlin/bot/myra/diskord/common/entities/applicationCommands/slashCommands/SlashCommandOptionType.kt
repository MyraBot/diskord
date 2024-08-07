package bot.myra.diskord.common.entities.applicationCommands.slashCommands

import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.entities.message.Attachment
import bot.myra.diskord.common.entities.user.User
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

/**
 * [Documentation](https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-type)
 *
 * @property value Discords option type index.
 */
@Serializable(with = SlashCommandOptionType.Serializer::class)
enum class SlashCommandOptionType(val value: Int, val clazz: KClass<*> = Unit::class) {

    SUB_COMMAND(1),
    SUB_COMMAND_GROUP(2),
    STRING(3, String::class),
    INTEGER(4, Int::class),
    BOOLEAN(5, Boolean::class),
    USER(6, User::class),
    CHANNEL(7, ChannelData::class),
    ROLE(8, Role::class),
    MENTIONABLE(9),
    NUMBER(10, Long::class),
    ATTACHMENT(11, Attachment::class);

    internal object Serializer : KSerializer<SlashCommandOptionType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SlashCommandOptionType", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: SlashCommandOptionType) = encoder.encodeInt(value.value)
        override fun deserialize(decoder: Decoder): SlashCommandOptionType = decoder.decodeInt().let { type -> values().first { it.value == type } }
    }

    companion object {
        fun isArgument(optionType: SlashCommandOptionType): Boolean =
            listOf(STRING, INTEGER, BOOLEAN, USER, CHANNEL, ROLE, MENTIONABLE, NUMBER, ATTACHMENT)
                .map { it.value }
                .contains(optionType.value)

        inline fun <reified T> fromClass(): SlashCommandOptionType = values().first { it.clazz == T::class }
    }

}