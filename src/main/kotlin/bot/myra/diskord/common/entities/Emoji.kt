package bot.myra.diskord.common.entities

import bot.myra.diskord.common.utilities.Mention
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#button-object)
 *
 * @property id
 * @property name
 */
@Suppress("unused")
@Serializable
data class Emoji(
        val id: String? = null,
        val name: String? = null,
        val animated: Boolean = false
) {
    val mention: String get() = if (id == null) name!! else Mention.emoji(name!!, id, animated)
}