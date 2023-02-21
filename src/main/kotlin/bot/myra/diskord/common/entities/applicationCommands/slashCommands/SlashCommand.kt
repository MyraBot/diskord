package bot.myra.diskord.common.entities.applicationCommands.slashCommands

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.rest.behaviors.Entity
import kotlinx.serialization.Serializable

data class SlashCommand(
    val data: SlashCommandData,
    override val diskord: Diskord
) : Entity {
    override val id get() = data.id
    val name get() = data.name
    val options get() = data.options
    val resolved get( ) = data.resolved
}

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-data-structure)
 *
 * @property id The command id.
 * @property name The name and executor of the command.
 */
@Serializable
data class SlashCommandData(
    val id: String,
    val name: String,
    val options: List<SlashCommandOptionData> = emptyList(),
    val resolved: ResolvedData = ResolvedData(hashMapOf(), hashMapOf(), hashMapOf(), hashMapOf(), hashMapOf()),
)