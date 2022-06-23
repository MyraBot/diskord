package bot.myra.diskord.rest.behaviors.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.bodies.DeleteMessagesBody
import bot.myra.diskord.rest.modifiers.message.components.MessageModifier
import bot.myra.diskord.rest.request.RestClient

@Suppress("unused")
interface TextChannelBehavior {

    val data: ChannelData

    suspend fun send(vararg files: File = emptyArray(), message: MessageModifier): Message? {
        val msg = message.apply { transform() }
        return RestClient.executeNullable(Endpoints.createMessage) {
            json = msg.toJson()
            attachments = files.toList()
            arguments { arg("channel.id", data.id) }
        }
    }

    suspend fun send(vararg files: File = emptyArray(), message: suspend MessageModifier.() -> Unit): Message? {
        return send(files = files, message = MessageModifier().also { message.invoke(it) })
    }

    suspend fun send(vararg files: File): Message? {
        return send(files = files, message = MessageModifier())
    }

    suspend fun getMessage(id: String): Message? = Diskord.getMessage(data.id, id)

    suspend fun getMessages(before: String? = null, max: Int = 100): List<Message> = Diskord.getMessages(data.id, max, before)

    suspend fun deleteMessages(ids: List<String>, reason: String? = null) = RestClient.execute(Endpoints.bulkDeleteMessages) {
        if (ids.size < 2) throw IllegalArgumentException("There must be at least 2 messages to delete")
        if (ids.size > 100) throw IllegalArgumentException("You can't delete more than 100 messages")
        json = DeleteMessagesBody(ids).toJson()
        logReason = reason
        arguments { arg("channel.id", data.id) }
    }

}