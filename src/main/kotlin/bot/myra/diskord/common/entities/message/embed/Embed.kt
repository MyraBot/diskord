package bot.myra.diskord.common.entities.message.embed

import bot.myra.diskord.common.serializers.SInstant
import bot.myra.diskord.common.utilities.ColorIntSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.awt.Color
import java.time.Instant

@Suppress("unused")
@Serializable
data class Embed(
    var title: String? = null,
    var type: String? = null,
    var description: String? = null,
    var url: String? = null,
    @Serializable(with = SInstant::class) var timestamp: Instant? = null,
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