package bot.myra.diskord.common.entities.applicationCommands.components

import bot.myra.diskord.common.entities.Emoji
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#component-object-component-structure)
 *
 * @property type Component type.
 * @property disabled Whether the component is disabled.
 * @property style A [ButtonStyle] entry.
 * @property label Text that appears on the button
 * @property emoji An [Emoji] object, only requires [Emoji.name], [Emoji.id] and [Emoji.animated].
 * @property id Custom set id.
 * @property url An url for [ButtonStyle.LINK] buttons.
 * @property options Pre-made [SelectOption]s to choose from.
 * @property placeholder A placeholder text.
 * @property minValues A minimum number (0-25) of items that must be chosen.
 * @property maxValues A maximum number (0-25) of items that must be chosen.
 */
@Serializable
open class Component(
    open val type: ComponentType,
    open var disabled: Boolean = false,
    open var style: ButtonStyle? = null,
    open var label: String? = null,
    open var emoji: Emoji? = null,
    @SerialName("custom_id") open var id: String? = null,
    open var url: String? = null,
    open var options: MutableList<SelectOption> = mutableListOf(),
    open var placeholder: String? = null,
    @SerialName("min_values") open var minValues: Int? = null,
    @SerialName("max_values") open var maxValues: Int? = null,
    open var components: MutableList<Component> = mutableListOf()
) {
    /**
     * @return Return a boolean whether the last [components] entry is full or not.
     */
    fun isFull(): Boolean {
        return when {
            this.components.size == 0                                                                          -> false
            this.components.find { it.type == ComponentType.BUTTON } != null && this.components.size == 5      -> true
            this.components.find { it.type == ComponentType.SELECT_MENU } != null && this.components.size == 1 -> true
            else                                                                                               -> false
        }
    }

    fun asActionRow(): ActionRow {
        val row = ActionRow()
        row.components = components
        return row
    }

    fun asButton(): Button {
        if (id == null && url == null) throw IllegalStateException("A button needs an id or a url")
        val button = style?.let { Button(style = it) } ?: throw IllegalStateException("The button style can't be null")
        button.label = label
        button.emoji = emoji
        button.id = id
        button.url = url
        button.disabled = disabled
        return button
    }

    fun asSelectMenu(): SelectMenu {
        val menu = SelectMenu()
        menu.id = id
        menu.options = options
        menu.placeholder = placeholder
        menu.minValues = minValues
        menu.maxValues = maxValues
        menu.disabled = disabled
        return menu
    }

}