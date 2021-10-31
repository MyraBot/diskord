package com.github.myraBot.diskord.common.entityData.interaction.components.items.selectMenu

import com.github.myraBot.diskord.common.entityData.interaction.EmojiData
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#select-menu-object-select-option-structure)
 */
@Serializable
data class SelectOption(
        val label: String,
        val value: String,
        val description: String? = null,
        val emoji: EmojiData? = null,
        val default: Boolean? = null
)
