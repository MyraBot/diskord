package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.JSON
import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.common.entities.Locale
import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionCallbackData
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionCallbackType
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionResponseData
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.builders.InteractionMessageBuilder
import com.github.myraBot.diskord.rest.interactionTransform
import com.github.myraBot.diskord.rest.request.Promise
import kotlinx.serialization.encodeToString

interface InteractionCreateBehavior {

    val interaction: Interaction

    val locale: Locale? get() = interaction.locale.value
    val guildLocale: Locale? get() = interaction.guildLocale.value

    suspend fun acknowledge(): Promise<Unit> {
        val json = JSON.encodeToString(InteractionResponseData(InteractionCallbackType.DEFERRED_UPDATE_MESSAGE))
        return Promise.of(Endpoints.acknowledgeInteraction, json) {
            arg("interaction.id", interaction.id)
            arg("interaction.token", interaction.token)
        }
    }

    suspend fun acknowledge(vararg files: File = emptyArray(), message: InteractionMessageBuilder): Promise<Unit> {
        val responseData = InteractionResponseData(
            InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE,
            InteractionCallbackData.fromMessageBuilder(message.interactionTransform())
        )
        val json = JSON.encodeToString(responseData)
        return Promise.of(Endpoints.acknowledgeInteraction, json, files.toList()) {
            arg("interaction.id", interaction.id)
            arg("interaction.token", interaction.token)
        }
    }

    suspend fun acknowledge(vararg files: File = emptyArray(), message: suspend InteractionMessageBuilder.() -> Unit): Promise<Unit> {
        return acknowledge(files = files, message = InteractionMessageBuilder(interaction).apply { message.invoke(this) })
    }

    suspend fun getInteractionResponse(): Promise<Message> {
        return Promise.of(Endpoints.getOriginalInteractionResponse) {
            arg("application.id", Diskord.id)
            arg("interaction.token", interaction.token)
        }
    }

}