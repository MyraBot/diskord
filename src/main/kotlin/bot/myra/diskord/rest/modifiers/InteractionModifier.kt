package bot.myra.diskord.rest.modifiers

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.rest.modifiers.message.components.GenericMessageModifier
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Wrapper for interaction modifiers.
 * You do not create an object of this class. Instead, use modifiers for explicit
 * interactions, like [bot.myra.diskord.rest.modifiers.message.components.MessageModifier]
 * for editing messages.
 *
 * @property interaction Received interaction, wich the response belongs to.
 * @property allowedMentions
 * @property flags
 * @constructor Create empty Interaction modifier
 */
@Serializable
class InteractionModifier(
    @Transient val interaction: Interaction? = null
) : GenericMessageModifier() {

    suspend fun transform() {
        val transform = Diskord.transformer
        content?.let { content = Diskord.transformer.onInteractionText(this, it) }
        embeds.map { transform.onInteractionEmbed(this, it) }
            .onEach { embed ->
                embed.title?.let { embed.title == transform.onInteractionText(this, it) }
                embed.author?.name?.let { embed.author!!.name = transform.onInteractionText(this, it) }
                embed.description?.let { embed.description = transform.onInteractionText(this, it) }
                embed.fields.forEach { field ->
                    field.name.let { field.name = transform.onInteractionText(this, it) }
                    field.value.let { field.value = transform.onInteractionText(this, it) }
                }
                embed.footer?.text?.let { embed.footer!!.text = transform.onInteractionText(this, it) }
            }
    }

}