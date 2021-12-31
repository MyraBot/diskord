package diskord.rest.behaviors

import diskord.Diskord
import diskord.common.entities.File
import diskord.common.entities.applicationCommands.Interaction
import diskord.common.entities.applicationCommands.InteractionCallbackData
import diskord.common.entities.applicationCommands.InteractionCallbackType
import diskord.common.entities.applicationCommands.InteractionResponseData
import diskord.common.entities.message.Message
import diskord.rest.Endpoints
import diskord.rest.builders.MessageBuilder
import diskord.utilities.JSON
import kotlinx.serialization.encodeToString

interface InteractionCreateBehavior {

    val interaction: Interaction

    suspend fun acknowledge() {
        val json = JSON.encodeToString(InteractionResponseData(InteractionCallbackType.DEFERRED_UPDATE_MESSAGE))
        Endpoints.acknowledgeInteraction.execute(json) {
            arg("interaction.id", interaction.id)
            arg("interaction.token", interaction.token)
        }
    }

    suspend fun acknowledge(vararg files: File = emptyArray(), message: MessageBuilder) {
        val responseData = InteractionResponseData(
            InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE,
            InteractionCallbackData.fromMessageBuilder(message.transform())
        )
        val json = JSON.encodeToString(responseData)

        Endpoints.acknowledgeInteraction.execute(json, files.toList()) {
            arg("interaction.id", interaction.id)
            arg("interaction.token", interaction.token)
        }
    }

    suspend fun acknowledge(vararg files: File = emptyArray(), message: suspend MessageBuilder.() -> Unit) = acknowledge(files = files, message = MessageBuilder().apply { message.invoke(this) })

    suspend fun getInteractionResponse(): Message? {
        return Endpoints.getOriginalInteractionResponse.execute {
            arg("application.id", Diskord.id)
            arg("interaction.token", interaction.token)
        }
    }

}