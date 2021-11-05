package com.github.myraBot.diskord.common.entityData.interaction.interactionTypes

import com.github.myraBot.diskord.rest.builders.ComponentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-data-structure)
 */
@Serializable
data class InteractionComponentData(
        val id: String? = null,
        val name: String? = null,
        val type: Int? = null,
        @SerialName("custom_id") val customId: String? = null,
        @SerialName("component_type") val componentType: ComponentType? = null
)