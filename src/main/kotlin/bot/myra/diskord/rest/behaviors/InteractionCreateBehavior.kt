package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.entities.Locale
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

    val locale: Locale? get() = interaction.locale.value
    val guildLocale: Locale? get() = interaction.guildLocale.value

    suspend fun acknowledge():Unit {
        return RestClient.execute(Endpoints.acknowledgeInteraction) {
            json = InteractionResponseData(InteractionCallbackType.DEFERRED_UPDATE_MESSAGE).toJson()
            arguments {
                arg("interaction.id", interaction.id)
                arg("interaction.token", interaction.token)
            }
        }
    }

    suspend fun acknowledge(vararg files: File = emptyArray(), message: InteractionModifier):Unit {
        val responseData = InteractionResponseData(
            InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE,
            message.apply { transform() }
        )
        return RestClient.execute(Endpoints.acknowledgeInteraction) {
            json = responseData.toJson()
            attachments = files.toList()
            arguments {
                arg("interaction.id", interaction.id)
                arg("interaction.token", interaction.token)
            }
        }
    }

    suspend fun acknowledge(vararg files: File = emptyArray(), message: suspend InteractionModifier.() -> Unit):Unit {
        return acknowledge(files = files, message = InteractionModifier(interaction).apply { message.invoke(this) })
    }

    suspend fun getInteractionResponse():Message {
        return RestClient.execute(Endpoints.getOriginalInteractionResponse) {
            arguments {
                arg("application.id", Diskord.id)
                arg("interaction.token", interaction.token)
            }
        }
    }

}