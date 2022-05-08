package bot.myra.diskord.common.entities.applicationCommands.components

import bot.myra.diskord.common.entities.Emoji

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#button-object)
 *
 * @property type Component type.
 * @property style A [ButtonStyle] entry.
 * @property label Text that appears on the button
 * @property emoji An [Emoji] object, only requires [Emoji.name], [Emoji.id] and [Emoji.animated].
 * @property id Custom set id.
 * @property url An url for [ButtonStyle.LINK] buttons.
 * @property disabled Whether the button is disabled.
 */
data class Button(
    override val type: ComponentType = ComponentType.BUTTON,
    public override var style: ButtonStyle? = null,
    public override var label: String? = null,
    public override var emoji: Emoji? = null,
    public override var id: String? = null,
    public override var url: String? = null,
    public override var disabled: Boolean = false
) : Component(type)