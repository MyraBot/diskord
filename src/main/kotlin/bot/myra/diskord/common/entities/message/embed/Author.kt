package bot.myra.diskord.common.entities.message.embed

import bot.myra.diskord.common.entities.MessageMarker
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#embed-object-embed-footer-structure)
 *
 * @property name The name of the author. Displayed as the text.
 * @property url Url as hyperlink.
 * @property icon Url of author icon.
 */
@MessageMarker
@Serializable
data class Author(
        var name: String,
        var url: String? = null,
        @SerialName("icon_url") var icon: String? = null
)
