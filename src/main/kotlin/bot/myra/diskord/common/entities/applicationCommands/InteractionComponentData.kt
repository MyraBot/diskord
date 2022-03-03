package bot.myra.diskord.common.entities.applicationCommands

import bot.myra.diskord.common.entities.applicationCommands.components.items.ComponentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-data-structure)
 */
@Serializable
data class InteractionComponentData(
        @SerialName("custom_id") val customId: String,
        @SerialName("component_type") val componentType: ComponentType
)