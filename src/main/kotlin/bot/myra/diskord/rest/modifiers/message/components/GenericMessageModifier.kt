package bot.myra.diskord.rest.modifiers.message.components

import bot.myra.diskord.common.entities.applicationCommands.components.Component
import bot.myra.diskord.common.entities.message.embed.Embed
import kotlinx.serialization.Serializable

@Serializable
abstract class GenericMessageModifier(
    var content: String? = null,
    var tts: Boolean? = null,
    override var embeds: MutableList<Embed> = mutableListOf(),
    override var components: MutableList<Component> = mutableListOf(),
) : IComponentModifier, IEmbedModifier