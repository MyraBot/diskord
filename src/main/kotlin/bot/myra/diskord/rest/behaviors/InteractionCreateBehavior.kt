package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionCallbackType
import bot.myra.diskord.common.entities.applicationCommands.InteractionResponseData
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.modifiers.InteractionModifier
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

interface InteractionCreateBehavior {

    val interaction: Interaction

    val locale: Locale? get() = interaction.locale.value
    val guildLocale: Locale? get() = interaction.guildLocale.value

    suspend fun acknowledgeAsync(): Deferred<Unit> {
        return RestClient.executeAsync(Endpoints.acknowledgeInteraction) {
            json = InteractionResponseData(InteractionCallbackType.DEFERRED_UPDATE_MESSAGE).toJson()
            arguments {
                arg("interaction.id", interaction.id)
                arg("interaction.token", interaction.token)
            }
        }
    }

    suspend fun acknowledgeAsync(vararg files: File = emptyArray(), message: InteractionModifier): Deferred<Unit> {
        val responseData = InteractionResponseData(
            InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE,
            message.apply { transform() }
        )
        return RestClient.executeAsync(Endpoints.acknowledgeInteraction) {
            json = responseData.toJson()
            attachments = files.toList()
            arguments {
                arg("interaction.id", interaction.id)
                arg("interaction.token", interaction.token)
            }
        }
    }

    suspend fun acknowledgeAsync(vararg files: File = emptyArray(), message: suspend InteractionModifier.() -> Unit): Deferred<Unit> {
        return acknowledgeAsync(files = files, message = InteractionModifier(interaction).apply { message.invoke(this) })
    }

    suspend fun getInteractionResponseAsync(): Deferred<Message> {
        return RestClient.executeAsync(Endpoints.getOriginalInteractionResponse) {
            arguments {
                arg("application.id", Diskord.id)
                arg("interaction.token", interaction.token)
            }
        }
    }

}