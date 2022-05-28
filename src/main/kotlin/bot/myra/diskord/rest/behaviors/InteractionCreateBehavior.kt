package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionCallbackType
import bot.myra.diskord.common.entities.applicationCommands.InteractionResponseData
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.modifiers.InteractionModifier
import bot.myra.diskord.rest.request.RestClient

interface InteractionCreateBehavior {

    val interaction: Interaction

    suspend fun acknowledge() = RestClient.execute(Endpoints.acknowledgeInteraction) {
        json = InteractionResponseData(InteractionCallbackType.DEFERRED_UPDATE_MESSAGE).toJson()
        arguments {
            arg("interaction.id", interaction.id)
            arg("interaction.token", interaction.token)
        }
    }

    suspend fun acknowledge(vararg files: File = emptyArray(), message: InteractionModifier) = RestClient.execute(Endpoints.acknowledgeInteraction) {
        json = InteractionResponseData(InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE, message.apply { transform() }).toJson()
        attachments = files.toList()
        arguments {
            arg("interaction.id", interaction.id)
            arg("interaction.token", interaction.token)
        }
    }

    suspend fun acknowledge(vararg files: File = emptyArray(), message: suspend InteractionModifier.() -> Unit) =
        acknowledge(files = files, message = interaction.asModifier().apply { message.invoke(this) })

    suspend fun thinking() = RestClient.execute(Endpoints.acknowledgeInteraction) {
        json = InteractionResponseData(InteractionCallbackType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE).toJson()
        arguments {
            arg("interaction.id", interaction.id)
            arg("interaction.token", interaction.token)
        }
    }

    suspend fun editOriginal(vararg files: File = emptyArray(), message: suspend InteractionModifier.() -> Unit) =
        editOriginal(files.asList(), interaction.asModifier().apply { message.invoke(this) })

    suspend fun editOriginal(files: List<File> = emptyList(), message: InteractionModifier) = RestClient.execute(Endpoints.acknowledgeOriginalResponse) {
        json = message.apply { transform() }.toJson()
        arguments {
            arg("application.id", Diskord.id)
            arg("interaction.token", interaction.token)
        }
    }

    /**
     * Edits the original [Interaction.message].
     * Overwrites the old message entirely.
     *
     * @param message The new interaction.
     */
    suspend fun edit(message: InteractionModifier) = RestClient.execute(Endpoints.acknowledgeInteraction) {
        json = InteractionResponseData(InteractionCallbackType.UPDATE_MESSAGE, message.apply { transform() }).toJson()
        arguments {
            arg("interaction.id", interaction.id)
            arg("interaction.token", interaction.token)
        }
    }

    suspend fun edit(modifier: suspend InteractionModifier.() -> Unit) = edit(interaction.asFollowupModifier().apply { modifier.invoke(this) })

    suspend fun getInteractionResponse(): Message = RestClient.execute(Endpoints.getOriginalInteractionResponse) {
        arguments {
            arg("application.id", Diskord.id)
            arg("interaction.token", interaction.token)
        }
    }

}