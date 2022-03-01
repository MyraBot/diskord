package bot.myra.diskord.common.entities.applicationCommands

import bot.myra.diskord.common.entities.applicationCommands.components.Component
import bot.myra.diskord.common.entities.message.Attachment
import bot.myra.diskord.common.entities.message.embed.Embed
import bot.myra.diskord.rest.builders.MessageBuilder
import bot.myra.diskord.rest.transform
import kotlinx.serialization.Serializable

@Serializable
data class InteractionCallbackData(
    val tts: Boolean? = null,
    val content: String? = null,
    val embeds: MutableList<Embed>? = null,
    val components: MutableList<Component>? = null,
    val attachments: MutableList<Attachment>? = null
) {
    companion object {
        suspend fun fromMessageBuilder(messageBuilder: MessageBuilder): InteractionCallbackData =
            messageBuilder.transform().let { builder ->
                InteractionCallbackData(
                    tts = builder.tts ?: false,
                    content = builder.content,
                    embeds = builder.embeds,
                    components = builder.actionRows,
                )
            }
    }

}
