package bot.myra.diskord.rest.behaviors.interaction

import bot.myra.diskord.common.entities.applicationCommands.ChoiceResponses
import bot.myra.diskord.common.entities.applicationCommands.InteractionCallbackType
import bot.myra.diskord.common.entities.applicationCommands.InteractionChoiceResponseData
import bot.myra.diskord.common.entities.applicationCommands.InteractionData
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommandChoice
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.behaviors.DefaultBehavior

interface AutoCompleteBehavior : DefaultBehavior {
    val data: InteractionData

    suspend fun suggestChoices(choices: List<SlashCommandChoice>) = diskord.rest.execute(Endpoints.acknowledgeInteraction) {
        json = InteractionChoiceResponseData(
            InteractionCallbackType.APPLICATION_COMMAND_AUTOCOMPLETE_RESULT,
            ChoiceResponses(choices)
        ).toJson()
        arguments {
            arg("interaction.id", data.id)
            arg("interaction.token", data.token)
        }
    }

}