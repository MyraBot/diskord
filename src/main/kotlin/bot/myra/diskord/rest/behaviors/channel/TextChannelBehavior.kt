package bot.myra.diskord.rest.behaviors.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.modifiers.message.components.MessageModifier
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

@Suppress("unused")
interface TextChannelBehavior {

    val data: ChannelData

    suspend fun sendAsync(vararg files: File = emptyArray(), message: MessageModifier): Deferred<Message?> {
        val msg = message.apply { transform() }
        return RestClient.executeNullableAsync(Endpoints.createMessage) {
            json = msg.toJson()
            attachments = files.toList()
            arguments { arg("channel.id", data.id) }
        }
    }

    suspend fun sendAsync(vararg files: File = emptyArray(), message: suspend MessageModifier.() -> Unit): Deferred<Message?> {
        return sendAsync(files = files, message = MessageModifier().also { message.invoke(it) })
    }

    suspend fun sendAsync(vararg files: File): Deferred<Message?> {
        return sendAsync(files = files, message = MessageModifier())
    }

    suspend fun getMessageAsync(id: String): Deferred<Message?> = Diskord.getMessageAsync(data.id, id)

}