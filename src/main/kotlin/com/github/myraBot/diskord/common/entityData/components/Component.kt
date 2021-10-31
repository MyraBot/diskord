package com.github.myraBot.diskord.common.entitythis.interaction.components

import com.github.myraBot.diskord.common.entityData.EmojiData
import com.github.myraBot.diskord.common.entityData.components.items.ActionRowData
import com.github.myraBot.diskord.common.entityData.components.items.button.ButtonData
import com.github.myraBot.diskord.common.entityData.components.items.button.ButtonStyle
import com.github.myraBot.diskord.common.entityData.components.items.selectMenu.SelectOption
import com.github.myraBot.diskord.rest.builders.ComponentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#component-object-component-structure)
 */
@Serializable
data class Component(
        var type: Int = 1,
        @SerialName("custom_id") var id: String? = null,
        var disabled: Boolean = false,
        var style: ButtonStyle? = null,
        var label: String? = null,
        var emoji: EmojiData? = null,
        var url: String? = null,
        val options: List<SelectOption>? = null,
        val placeholder: String? = null,
        @SerialName("min_values") val minValues: Int? = null,
        @SerialName("max_values") val maxValues: Int? = null,
        val components: MutableList<Component> = mutableListOf() // Items must be action rows
) {
    /**
     * @return Return a boolean whether the last [components] entry is full or not.
     */
    fun isFull(): Boolean {
        return when {
            this.components.size == 0 -> false
            this.components.find { it.type == ComponentType.button } != null && this.components.size == 5 -> true
            this.components.find { it.type == ComponentType.selectMenu } != null && this.components.size == 1 -> true
            else -> false
        }
    }
}

fun ActionRowData.asComponent(): Component {
    return Component(
        type = this.type,
    )
}

fun ButtonData.asComponent(): Component {
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