package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionCallbackType
import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionCallbackData
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionResponseData
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

    suspend fun acknowledge(message: MessageBuilder.() -> Unit) {
        val responseData = InteractionResponseData(
            InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE,
            InteractionCallbackData.fromMessageBuilder(MessageBuilder().apply(message))
        )
        val json = JSON.encodeToString(responseData)

        Endpoints.acknowledgeInteraction.execute(json) {
            arg("interaction.id", interaction.id)
            arg("interaction.token", interaction.token)
        }
    }

}