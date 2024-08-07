package bot.myra.diskord.rest.modifiers.message.components

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.components.Component
import bot.myra.diskord.common.entities.message.Attachment
import bot.myra.diskord.common.entities.message.MessageFlag
import bot.myra.diskord.common.entities.message.MessageFlags
import bot.myra.diskord.common.entities.message.embed.Embed
import kotlinx.serialization.Serializable

@Serializable
abstract class GenericMessageModifier(
    var content: String? = null,
    var tts: Boolean? = null,
    override var embeds: MutableList<Embed> = mutableListOf(),
    val flags: MessageFlags = MessageFlags(),
    override var components: MutableList<Component> = mutableListOf(),
    var attachments: MutableList<Attachment> = mutableListOf(),
) : IComponentModifier, IEmbedModifier {

    @Suppress("unused")
    fun ephemeral() = flags.add(MessageFlag.EPHEMERAL)

    abstract suspend fun transform(diskord: Diskord)

}