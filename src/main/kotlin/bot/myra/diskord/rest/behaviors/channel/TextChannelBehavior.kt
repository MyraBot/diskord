package bot.myra.diskord.rest.behaviors.channel

import bot.myra.diskord.common.entities.File
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.behaviors.DiskordObject
import bot.myra.diskord.rest.bodies.DeleteMessagesBody
import bot.myra.diskord.rest.modifiers.message.components.MessageModifier
import bot.myra.diskord.rest.request.Result

@Suppress("unused")
interface TextChannelBehavior : DiskordObject {
    val data: ChannelData

    suspend fun send(vararg files: File = emptyArray(), message: MessageModifier): Result<Message> {
        val msg = message.apply { transform(diskord) }
        return diskord.rest.execute(Endpoints.createMessage) {
            json = msg.toJson()
            attachments = files.toList()
            arguments { arg("channel.id", data.id) }
        }.transformValue { Message(it, diskord) }
    }

    suspend fun send(vararg files: File = emptyArray(), message: suspend MessageModifier.() -> Unit): Result<Message> {
        return send(files = files, message = MessageModifier().also { message.invoke(it) })
    }

    suspend fun send(vararg files: File): Result<Message> {
        return send(files = files, message = MessageModifier())
    }

    suspend fun getMessage(id: String): Result<Message> = diskord.getMessage(data.id, id)

    suspend fun getMessages(max: Int = 100, before: String? = null, after: String? = null) = diskord.fetchMessages(data.id, max, before, after)

    suspend fun deleteMessages(ids: List<String>, reason: String? = null) = diskord.rest.execute(Endpoints.bulkDeleteMessages) {
        if (ids.size < 2) throw IllegalArgumentException("There must be at least 2 messages to delete")
        if (ids.size > 100) throw IllegalArgumentException("You can't delete more than 100 messages")
        json = DeleteMessagesBody(ids).toJson()
        logReason = reason
        arguments { arg("channel.id", data.id) }
    }

}