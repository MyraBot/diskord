package bot.myra.diskord.common.entities.applicationCommands

import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommandOptionType
import kotlinx.serialization.Serializable

@Serializable
data class AutoCompleteOption(
    val type: SlashCommandOptionType,
    val name: String,
    val id: String? = null,
    val options: List<AutoCompleteOption>? = null,
    val value: String? = null,
    val focused: Boolean? = null
)