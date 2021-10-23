package com.github.myraBot.diskord.common.entities.embed

import com.github.myraBot.diskord.utilities.ColourSerializer
import com.github.myraBot.diskord.utilities.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.awt.Color
import java.time.Instant

@Serializable
data class Embed(
        var title: String? = null,
        var type: String? = null,
        var description: String? = null,

        var url: String? = null,
        @Serializable(with = InstantSerializer::class) val timestamp: Instant? = null,
        @SerialName("colour") @Serializable(with = ColourSerializer::class) var colour: Color? = null,

        var footer: Footer? = null,
        var image: String? = null,
        var thumbnail: Thumbnail? = null,
        var author: Author? = null,
)

suspend fun embed(builder: suspend Embed.() -> Unit): Embed = Embed().apply { builder.invoke(this) }