package bot.myra.diskord.common.entities.applicationCommands

import bot.myra.diskord.rest.modifiers.InteractionModifier
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-interaction-response-structure)
 *
 * @property type Type of interaction response.
 * @property data Actual message data
 */
@Serializable
data class InteractionResponseData(
        val type: InteractionCallbackType,
        val data: InteractionModifier? = null
)