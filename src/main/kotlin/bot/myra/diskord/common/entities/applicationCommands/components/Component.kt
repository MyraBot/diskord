package bot.myra.diskord.common.entities.applicationCommands.components

import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.applicationCommands.components.items.ActionRowData
import bot.myra.diskord.common.entities.applicationCommands.components.items.button.Button
import bot.myra.diskord.common.entities.applicationCommands.components.items.button.ButtonStyle
import bot.myra.diskord.common.entities.applicationCommands.components.items.button.SelectMenu
import bot.myra.diskord.common.entities.applicationCommands.components.items.selectMenu.SelectOption
import bot.myra.diskord.common.entities.applicationCommands.components.items.ComponentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#component-object-component-structure)
 */
@Serializable
data class Component(
    @Serializable(with = ComponentType.Serializer::class) var type: ComponentType,
    @SerialName("custom_id") var id: String? = null,
    var disabled: Boolean = false,
    var style: ButtonStyle? = null,
    var label: String? = null,
    var emoji: Emoji? = null,
    var url: String? = null,
    val options: List<SelectOption>? = null,
    var placeholder: String? = null,
    @SerialName("min_values") val minValues: Int? = null,
    @SerialName("max_values") val maxValues: Int? = null,
    val components: MutableList<Component> = mutableListOf()
) {
    /**
     * @return Return a boolean whether the last [components] entry is full or not.
     */
    fun isFull(): Boolean {
        return when {
            this.components.size == 0 -> false
            this.components.find { it.type == ComponentType.BUTTON } != null && this.components.size == 5 -> true
            this.components.find { it.type == ComponentType.SELECT_MENU } != null && this.components.size == 1 -> true
            else -> false
        }
    }

    fun asButton(): Button {
        return Button(
            style = style!!,
            label = label,
            emoji = emoji,
            id = id,
            url = url,
            disabled = disabled
        )
    }

    fun asSelectMenu(): SelectMenu {
        return SelectMenu(
            type = type,
            id = id!!,
            options = options!!,
            placeholder = placeholder,
            minValues = minValues!!,
            maxValues = maxValues!!,
            disabled = disabled
        )
    }

}

fun ActionRowData.asComponent(): Component {
    return Component(
        type = this.type,
    )
}

fun Button.asComponent(): Component {
    return Component(
        type = this.type,
        id = this.id,
        disabled = this.disabled,
        style = this.style,
        label = this.label,
        emoji = this.emoji,
        url = this.url
    )
}

fun SelectMenu.asComponent(): Component {
    return Component(
        type = this.type,
        id = this.id,
        options = this.options,
        placeholder = this.placeholder,
        maxValues = this.maxValues,
        minValues = this.minValues,
        disabled = this.disabled
    )
}