package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entityData.interaction.InteractionCallbackType
import com.github.myraBot.diskord.common.entityData.interaction.InteractionData
import com.github.myraBot.diskord.common.entityData.interaction.InteractionResponseData
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.utilities.JSON
import kotlinx.serialization.encodeToString
import java.util.concurrent.Flow

interface InteractionCreateBehavior {

    val interactionData: InteractionData

    suspend fun acknowledge() {
        val json = JSON.encodeToString(InteractionResponseData(InteractionCallbackType.DEFERRED_UPDATE_MESSAGE))
        Endpoints.acknowledgeInteraction.execute(json) {
            arg("interaction.id", interactionData.id)
            arg("interaction.token", interactionData.token)
        }
    }

}