package bot.myra.diskord.common.entities.applicationCommands.components.items

import bot.myra.diskord.common.entities.applicationCommands.components.Component
import kotlinx.serialization.Serializable

@Serializable
/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#action-rows)
 */
data class ActionRowData(
        val type: ComponentType = ComponentType.ACTION_ROW,
        val components: MutableList<Component> = mutableListOf()
)