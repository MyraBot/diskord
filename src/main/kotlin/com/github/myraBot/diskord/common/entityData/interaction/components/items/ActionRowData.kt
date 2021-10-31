package com.github.myraBot.diskord.common.entityData.interaction.components.items

import com.github.myraBot.diskord.common.entitythis.interaction.components.Component
import com.github.myraBot.diskord.rest.builders.ComponentType
import kotlinx.serialization.Serializable

@Serializable
/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#action-rows)
 */
data class ActionRowData(
        val type: Int = ComponentType.actionRow,
        val components: MutableList<Component> = mutableListOf()
)