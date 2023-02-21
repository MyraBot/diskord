package bot.myra.diskord.common.entities.message

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.components.Component
import bot.myra.diskord.common.entities.message.embed.Embed
import bot.myra.diskord.common.serializers.SInstant
import bot.myra.diskord.rest.behaviors.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

class UpdatedMessage(
    val data: UpdatedMessageData,
    override val diskord: Diskord
) : Entity {
    override val id: String get() = data.id
    val channelId get() = data.channelId
    val content get() = data.content
    val edited get() = data.edited
    val guildId get() = data.guildId
    val attachments get() = data.attachments
    val embeds get() = data.embeds
    val components get() = data.components
}

/**
 * @property id The message id.
 * @property channelId The id of the channel the message got sent to.
 * @property guildId An optional guild id.
 */
@Serializable
data class UpdatedMessageData(
    val id: String,
    @SerialName("channel_id") val channelId: String,
    val content: String? = null,
    @Serializable(with = SInstant::class) @SerialName("edited_timestamp") val edited: Instant? = null,
    @SerialName("guild_id") val guildId: String? = null,
    val attachments: List<Attachment>? = null,
    val embeds: List<Embed>? = null,
    val components: List<Component>? = null
)