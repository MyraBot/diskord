package bot.myra.diskord.common.entities.applicationCommands.components.items.button

import bot.myra.diskord.common.entities.applicationCommands.components.items.selectMenu.SelectOption
import bot.myra.diskord.common.entities.applicationCommands.components.items.ComponentType
import kotlinx.serialization.SerialName

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#select-menu-object-select-menu-structure)
 *
 * @property type Component type.
 * @property id A custom set id.
 * @property options Pre-made [SelectOption]s to choose from.
 * @property placeholder A placeholder text.
 * @property minValues A minimum number (0-25) of items that must be chosen.
 * @property maxValues A maximum number (0-25) of items that must be chosen.
 * @property disabled Whether the select menu is disabled.
 */
data class SelectMenu(
        val type: ComponentType = ComponentType.SELECT_MENU,
        @SerialName("custom_id") val id: String,
        val options: List<SelectOption>,
        val placeholder: String? = null,
        @SerialName("min_values") var minValues: Int = 1,
        @SerialName("max_values") var maxValues: Int = 1,
        val disabled: Boolean = false
)
