package com.github.myraBot.diskord.common.entities.message.embed

import com.github.myraBot.diskord.utilities.ColorIntSerializer
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
    @Serializable(with = InstantSerializer::class) var timestamp: Instant? = null,
    @SerialName("color") @Serializable(with = ColorIntSerializer::class) var colour: Color? = null,
    var footer: Footer? = null,
    var image: Image? = null,
    var thumbnail: Thumbnail? = null,
    var author: Author? = null,
    val fields: MutableList<Field> = mutableListOf(),
) {
    suspend fun addField(data: suspend Field.() -> Unit) {
        val field = Field("", "").also { data.invoke(it) }
        fields.add(field)
    }
}

suspend fun embed(builder: suspend Embed.() -> Unit): Embed = Embed().apply { builder.invoke(this) }