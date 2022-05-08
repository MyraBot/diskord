package bot.myra.diskord.common.entities.applicationCommands.components

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#action-rows)
 */
data class ActionRow(
    override val type: ComponentType = ComponentType.ACTION_ROW,
) : Component(type)