package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.utilities.ColourSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class Role(
        val id: String,
        val name: String,
        @SerialName("color") @Serializable(with = ColourSerializer::class) val colour: Color,
        @SerialName("hoist") val isShownSeparate: Boolean,
        val icon: String? = null,
        @SerialName("unicode_emoji") val unicodeEmoji: String? = null,
        val position: Int,
        @Serializable(with = Permission.Serializer::class) val permissions: List<Permission>,
        @SerialName("managed") val fromAnIntegration: Boolean,
        val mentionable: Boolean
)
