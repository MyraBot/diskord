package bot.myra.diskord.common.entities.applicationCommands.components

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
    override val type: ComponentType = ComponentType.SELECT_MENU,
    override var id: String?,
    override var options: MutableList<SelectOption> = mutableListOf(),
    override var placeholder: String? = null,
    override var minValues: Int = 1,
    override var maxValues: Int = 1,
    override var disabled: Boolean = false
) : Component(type)
