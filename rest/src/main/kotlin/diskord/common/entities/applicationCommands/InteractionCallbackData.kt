package diskord.common.entities.applicationCommands

import diskord.common.entities.applicationCommands.components.Component
import diskord.common.entities.message.Attachment
import diskord.common.entities.message.embed.Embed
import diskord.rest.builders.MessageBuilder
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

        fun fromMessageBuilder(messageBuilder: MessageBuilder): InteractionCallbackData = InteractionCallbackData(
            tts = messageBuilder.tts ?: false,
            content = messageBuilder.content,
            embeds = messageBuilder.embeds,
            components = messageBuilder.actionRows,
        )

    }

}
