package diskord.rest.behaviors.channel.impl

import diskord.common.entities.File
import diskord.common.entities.message.Message
import diskord.rest.Endpoints
import diskord.rest.behaviors.channel.ChannelBehavior
import diskord.rest.builders.MessageBuilder
import diskord.utilities.JSON
import kotlinx.serialization.encodeToString

interface TextChannelBehavior : ChannelBehavior {

    suspend fun send(vararg files: File = emptyArray(), message: suspend MessageBuilder.() -> Unit): Message = send(files = files, message = MessageBuilder().also { message.invoke(it) })

    suspend fun send(vararg files: File = emptyArray(), message: MessageBuilder): Message {
        val json = JSON.encodeToString(message.transform())
        return Endpoints.createMessage.executeNonNull(json, files.toList()) { arg("channel.id", id) }
    }

}