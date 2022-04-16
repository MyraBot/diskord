package bot.myra.diskord.common.entities.applicationCommands

import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommandChoice
import kotlinx.serialization.Serializable

@Serializable
data class InteractionChoiceResponseData(
    val type: InteractionCallbackType,
    val data: ChoiceResponses,
)

@Serializable
data class ChoiceResponses(
    val choices: List<SlashCommandChoice>
)