package bot.myra.diskord.rest.behaviors.channel

import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.builders.MessageBuilder
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred
import bot.myra.diskord.rest.transform

@Suppress("unused")
interface TextChannelBehavior {

    val data: ChannelData

    suspend fun sendAsync(vararg files: File = emptyArray(), message: MessageBuilder): Deferred<Message?> {
        val msg = message.transform()
        return RestClient.executeNullableAsync(Endpoints.createMessage) {
            json = msg.toJson()
            attachments = files.toList()
            arguments { arg("channel.id", data.id) }
        }
    }

    suspend fun sendAsync(vararg files: File = emptyArray(), message: suspend MessageBuilder.() -> Unit): Deferred<Message?> {
        return sendAsync(files = files, message = MessageBuilder().also { message.invoke(it) })
    }

    suspend fun sendAsync(vararg files: File): Deferred<Message?> {
        return sendAsync(files = files, message = MessageBuilder())
    }

}