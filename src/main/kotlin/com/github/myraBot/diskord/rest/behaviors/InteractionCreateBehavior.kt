package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.Diskord
import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionCallbackData
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionCallbackType
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionResponseData
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.builders.MessageBuilder
import com.github.myraBot.diskord.utilities.JSON
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