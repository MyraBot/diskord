package com.github.myraBot.diskord.common.entityData.components.items.button

import com.github.myraBot.diskord.common.entityData.EmojiData
import com.github.myraBot.diskord.rest.builders.ComponentType

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#button-object)
 *
 * @property type Integer type, for buttons 2.
 * @property style A [ButtonStyle] entry.
 * @property label Text that appears on the button
 * @property emoji An [EmojiData] object, only requires [EmojiData.name], [EmojiData.id] and [EmojiData.animated].
 * @property id Custom set id.
 * @property url An url for [ButtonStyle.LINK] buttons.
 * @property disabled Whether the button is disabled.
 */
data class ButtonData(
        val type: Int = ComponentType.button,
        var style: ButtonStyle,
        var label: String? = null,
        var emoji: EmojiData? = null,
        var id: String? = null,
        var url: String? = null,
        var disabled: Boolean = false
)