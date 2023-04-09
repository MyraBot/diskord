package bot.myra.diskord.rest.behaviors.interaction

import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionCallbackType
import bot.myra.diskord.common.entities.applicationCommands.InteractionData
import bot.myra.diskord.common.entities.applicationCommands.InteractionResponseData
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.behaviors.DefaultBehavior
import bot.myra.diskord.rest.modifiers.InteractionModifier

interface InteractionCreateBehavior : DefaultBehavior {
    val data: InteractionData
    val modifier: InteractionModifier
    val followupModifier: InteractionModifier

    suspend fun acknowledge() = diskord.rest.execute(Endpoints.acknowledgeInteraction) {
        json = InteractionResponseData(InteractionCallbackType.DEFERRED_UPDATE_MESSAGE).toJson()
        arguments {
            arg("interaction.id", data.id)
            arg("interaction.token", data.token)
        }
    }.value!!

    suspend fun acknowledge(vararg files: File = emptyArray(), message: InteractionModifier) = diskord.rest.execute(Endpoints.acknowledgeInteraction) {
        json = InteractionResponseData(InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE, message.apply { transform(diskord) }).toJson()
        if (files.any { it.bytes.size / 1000000 > 8 }) throw Exception("A file is too big")
        attachments = files.toList()
        arguments {
            arg("interaction.id", data.id)
            arg("interaction.token", data.token)
        }
    }.value!!

    suspend fun acknowledge(vararg files: File = emptyArray(), message: suspend InteractionModifier.() -> Unit) =
        acknowledge(files = files, message = modifier.apply { message.invoke(this) })

    suspend fun thinking() = diskord.rest.execute(Endpoints.acknowledgeInteraction) {
        json = InteractionResponseData(InteractionCallbackType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE).toJson()
        arguments {
            arg("interaction.id", data.id)
            arg("interaction.token", data.token)
        }
    }.value!!

    suspend fun editOriginal(vararg files: File = emptyArray(), message: suspend InteractionModifier.() -> Unit) =
        editOriginal(files.asList(), modifier.apply { message.invoke(this) })

    suspend fun editOriginal(files: List<File> = emptyList(), message: InteractionModifier) = diskord.rest.execute(Endpoints.acknowledgeOriginalResponse) {
        json = message.apply { transform(diskord) }.toJson()
        if (files.any { it.bytes.size / 1000000 > 8 }) throw Exception("A file is too big")
        attachments = files.toList()
        arguments {
            arg("application.id", diskord.id)
            arg("interaction.token", data.token)
        }
    }.value!!

    /**
     * Edits the original [Interaction.message].
     * Overwrites the old message entirely.
     *
     * @param message The new interaction.
     */
    suspend fun edit(message: InteractionModifier) = diskord.rest.execute(Endpoints.acknowledgeInteraction) {
        json = InteractionResponseData(InteractionCallbackType.UPDATE_MESSAGE, message.apply { transform(diskord) }).toJson()
        arguments {
            arg("interaction.id", data.id)
            arg("interaction.token", data.token)
        }
    }.value!!

    suspend fun edit(modifier: suspend InteractionModifier.() -> Unit) = edit(followupModifier.apply { modifier.invoke(this) })

    suspend fun getInteractionResponse(): Message {
        val data = diskord.rest.execute(Endpoints.getOriginalInteractionResponse) {
            arguments {
                arg("application.id", diskord.id)
                arg("interaction.token", data.token)
            }
        }.value!!
        return Message(data, diskord)
    }
}