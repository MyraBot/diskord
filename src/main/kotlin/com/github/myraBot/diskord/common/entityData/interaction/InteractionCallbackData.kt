package com.github.myraBot.diskord.common.entityData.interaction

import com.github.myraBot.diskord.common.entityData.components.Component
import com.github.myraBot.diskord.common.entityData.embed.Embed
import kotlinx.serialization.Serializable

@Serializable
data class InteractionCallbackData(
        val tts: Boolean,
        val content: String,
        val embeds: List<Embed>,
        val components: List<Component>
)
