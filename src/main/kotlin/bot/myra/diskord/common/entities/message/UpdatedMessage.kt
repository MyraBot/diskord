package bot.myra.diskord.common.entities.message

import bot.myra.diskord.rest.behaviors.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property id The message id.
 * @property channelId The id of the channel the message got sent to.
 * @property guildId An optional guild id.
 */
@Serializable
data class UpdatedMessage(
    override val id: String,
    @SerialName("channel_id") val channelId: String,
    @SerialName("guild_id") val guildId: String? = null
) : Entity