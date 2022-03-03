package bot.myra.diskord.rest.modifiers

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.components.Component
import bot.myra.diskord.common.entities.message.Attachment
import bot.myra.diskord.common.entities.message.MessageFlags
import bot.myra.diskord.common.entities.message.MessageFlagsSerializer
import bot.myra.diskord.common.entities.message.embed.Embed
import bot.myra.diskord.rest.modifiers.components.IComponentModifier
import bot.myra.diskord.rest.modifiers.components.IEmbedModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
data class InteractionModifier(
    @Transient val interaction: Interaction? = null,

    var tts: Boolean? = null,
    var content: String? = null,
    override var embeds: MutableList<Embed> = mutableListOf(),
    @SerialName("allowed_mentions") var allowedMentions: MutableList<String> = mutableListOf(),
    @kotlinx.serialization.Serializable(with = MessageFlagsSerializer::class) var flags: MutableList<MessageFlags> = mutableListOf(),
    override var components: MutableList<Component> = mutableListOf(),
    var attachments: MutableList<Attachment> = mutableListOf()
) : IComponentModifier, IEmbedModifier {

    suspend fun transform() {
        val transform = Diskord.transformer
        content?.let { content = Diskord.transformer.onInteractionText(this, it) }
        embeds.onEach { embed ->
            transform.onEmbed(embed)

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