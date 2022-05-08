package bot.myra.diskord.common.entities.applicationCommands.components

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#select-menu-object-select-menu-structure)
 *
 * @property type Component type.
 * @property id A custom set id.

 */
data class SelectMenu(
    override val type: ComponentType = ComponentType.SELECT_MENU,
) : Component(type)
