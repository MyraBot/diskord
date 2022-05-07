package bot.myra.diskord.rest.modifiers

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.message.MessageFlag
import bot.myra.diskord.common.entities.message.MessageFlags
import bot.myra.diskord.rest.modifiers.message.components.GenericMessageModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class InteractionModifier(
    @Transient val interaction: Interaction? = null,

    @SerialName("allowed_mentions") var allowedMentions: MutableList<String> = mutableListOf(),
    var flags: MessageFlags = MessageFlags()
) : GenericMessageModifier() {

    fun ephemeral() = flags.add(MessageFlag.EPHEMERAL)

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