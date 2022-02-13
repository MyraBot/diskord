package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.common.entities.Locale
import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionCallbackData
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionCallbackType
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionResponseData
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.common.toJson
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.builders.InteractionMessageBuilder
import com.github.myraBot.diskord.rest.interactionTransform
import com.github.myraBot.diskord.rest.request.promises.Promise

interface InteractionCreateBehavior {

    val interaction: Interaction

    val locale: Locale? get() = interaction.locale.value
    val guildLocale: Locale? get() = interaction.guildLocale.value

    suspend fun acknowledge(): Promise<Unit> {
        return Promise.of(Endpoints.acknowledgeInteraction) {
            json = InteractionResponseData(InteractionCallbackType.DEFERRED_UPDATE_MESSAGE).toJson()
            arguments {
                arg("interaction.id", interaction.id)
                arg("interaction.token", interaction.token)
            }
        }
    }

    suspend fun acknowledge(vararg files: File = emptyArray(), message: InteractionMessageBuilder): Promise<Unit> {
        val responseData = InteractionResponseData(
            InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE,
            InteractionCallbackData.fromMessageBuilder(message.interactionTransform())
        )
        return Promise.of(Endpoints.acknowledgeInteraction) {
            json = responseData.toJson()
            attachments = files.toList()
            arguments {
                arg("interaction.id", interaction.id)
                arg("interaction.token", interaction.token)
            }
        }
    }

    suspend fun acknowledge(vararg files: File = emptyArray(), message: suspend InteractionMessageBuilder.() -> Unit): Promise<Unit> {
        return acknowledge(files = files, message = InteractionMessageBuilder(interaction).apply { message.invoke(this) })
    }

    suspend fun getInteractionResponse(): Promise<Message> {
        return Promise.of(Endpoints.getOriginalInteractionResponse) {
            arguments {
                arg("application.id", Diskord.id)
                arg("interaction.token", interaction.token)
            }
        }
    }

}