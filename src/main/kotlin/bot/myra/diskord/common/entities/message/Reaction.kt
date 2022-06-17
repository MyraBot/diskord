package bot.myra.diskord.common.entities.message

import bot.myra.diskord.common.entities.Emoji
import kotlinx.serialization.Serializable

@Serializable
data class Reaction(
    val count: Int,
    val me: Boolean,
    val emoji: Emoji
)
