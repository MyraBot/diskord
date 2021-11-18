package com.github.myraBot.diskord.common.entities.applicationCommands

import com.github.myraBot.diskord.rest.builders.ComponentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-data-structure)
 */
@Serializable
data class InteractionData(
        val id: String,
        @SerialName("application_id") val applicationId: String,
        val type: Int,
        @SerialName("custom_id") val customId: String,
        @SerialName("component_type") val componentType: ComponentType
)