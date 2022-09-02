package bot.myra.diskord.common.entities.message

import bot.myra.diskord.common.entities.applicationCommands.components.Component
import bot.myra.diskord.common.entities.message.embed.Embed
import bot.myra.diskord.common.serializers.SInstant
import bot.myra.diskord.rest.behaviors.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * @property id The message id.
 * @property channelId The id of the channel the message got sent to.
 * @property guildId An optional guild id.
 */
@Serializable
data class UpdatedMessage(
    override val id: String,
    @SerialName("channel_id") val channelId: String,
    val content: String? = null,
    @Serializable(with = SInstant::class) @SerialName("edited_timestamp") val edited: Instant? = null,
    @SerialName("guild_id") val guildId: String? = null,
    val attachments: List<Attachment>? = null,
    val embeds: List<Embed>? = null,
    val components: List<Component>? = null
) : Entity