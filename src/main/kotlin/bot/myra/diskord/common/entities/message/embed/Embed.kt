package bot.myra.diskord.common.entities.message.embed

import bot.myra.diskord.common.serializers.SColor
import bot.myra.diskord.common.serializers.SInstant
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
    @SerialName("color") @Serializable(with = SColor::class) var colour: Color? = null,
    internal var footer: Footer? = null,
    @SerialName("image") private var _image: Image? = null,
    @SerialName("thumbnail") private var _thumbnail: Thumbnail? = null,
    internal var author: Author? = null,
    val fields: MutableList<Field> = mutableListOf(),
) {
    var thumbnail: String?
        get() = _thumbnail?.url
        set(value) {
            _thumbnail = value?.let { Thumbnail(it) }
        }

    var image: String?
        get() = _image?.url
        set(value) {
            _image = value?.let { Image(it) }
        }

    suspend fun author(data: suspend Author.() -> Unit) {
        this.author = Author("", null, null).apply { data.invoke(this) }
    }

    suspend fun addField(data: suspend Field.() -> Unit) {
        val field = Field("", "").apply { data.invoke(this) }
        fields.add(field)
    }

    suspend fun footer(data: suspend Footer.() -> Unit) {
        this.footer = Footer("", null).apply { data.invoke(this) }
    }

}

suspend fun embed(builder: suspend Embed.() -> Unit): Embed = Embed().apply { builder.invoke(this) }