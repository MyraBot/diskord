package bot.myra.diskord.common.entities.applicationCommands.components

import bot.myra.diskord.common.entities.Emoji
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#select-menu-object-select-option-structure)
 */
@Serializable
data class SelectOption(
        var label: String,
        var value: String,
        var description: String? = null,
        var emoji: Emoji? = null,
        var default: Boolean? = null
)
