package bot.myra.diskord.common.entities.applicationCommands.components

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#button-object)
 */
data class Button(
    override val type: ComponentType = ComponentType.BUTTON,
    override var style: ButtonStyle?,
    override var id: String? = null,
    override var url: String? = null
) : Component(type)