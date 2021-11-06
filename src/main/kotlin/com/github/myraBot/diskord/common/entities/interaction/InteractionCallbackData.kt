package com.github.myraBot.diskord.common.entities.interaction

import com.github.myraBot.diskord.common.entities.interaction.components.Component
import com.github.myraBot.diskord.common.entities.message.embed.Embed
import kotlinx.serialization.Serializable

@Serializable
data class InteractionCallbackData(
        val tts: Boolean,
        val content: String,
        val embeds: List<Embed>,
        val components: List<Component>
)
