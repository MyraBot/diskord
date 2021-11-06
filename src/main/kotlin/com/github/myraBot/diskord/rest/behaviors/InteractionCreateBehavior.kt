package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.interaction.InteractionCallbackType
import com.github.myraBot.diskord.common.entities.interaction.Interaction
import com.github.myraBot.diskord.common.entities.interaction.InteractionResponseData
import com.github.myraBot.diskord.rest.Endpoints
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

}