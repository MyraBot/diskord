package bot.myra.diskord.common.entities.applicationCommands.slashCommands

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

/**
 * [Documentation](https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-choice-structure)
 *
 * @property name Choice name.
 * @property value Value of the voice. Can be a string, integer or double, depending on the [SlashCommandOptionType].
 */
@Serializable
data class SlashCommandChoice(
    val name: String,
    val value: JsonPrimitive
)