package bot.myra.diskord.rest.modifiers.message.components

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.components.Component
import bot.myra.diskord.common.entities.message.embed.Embed
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
open class MessageModifier(
    var content: String? = null,
    var tts: Boolean? = null,
    override var embeds: MutableList<Embed> = mutableListOf(),
    override var components: MutableList<Component> = mutableListOf(),
) : IComponentModifier, IEmbedModifier {

    suspend fun transform() {
        val transform = Diskord.transformer
        content?.let { content = Diskord.transformer.onText(this, it) }
        embeds.onEach { embed ->
            transform.onEmbed(embed)

            embed.title?.let { embed.title == transform.onText(this, it) }
            embed.author?.name?.let { embed.author!!.name = transform.onText(this, it) }
            embed.description?.let { embed.description = transform.onText(this, it) }
            embed.fields.forEach { field ->
                field.name.let { field.name = transform.onText(this, it) }
                field.value.let { field.value = transform.onText(this, it) }
            }
            embed.footer?.text?.let { embed.footer!!.text = transform.onText(this, it) }
        }
    }

}