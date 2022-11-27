package bot.myra.diskord.common.entities.message.embed

import bot.myra.diskord.common.entities.MessageMarker
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#embed-object-embed-structure)
 */
@MessageMarker
@Serializable
data class Field(
    var name: String,
    var value: String,
    var inline: Boolean = false
)
