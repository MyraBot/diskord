package com.github.myraBot.diskord.common.entityData.interaction.interactionTypes

import kotlinx.serialization.SerialName

data class ButtonInteractionDataUnused(
        @SerialName("custom_id") val customId: String,
        val componentType: Int
)