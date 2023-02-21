package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.message.UpdatedMessage
import bot.myra.diskord.common.entities.message.UpdatedMessageData
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

open class MessageUpdateEvent(
    val message: UpdatedMessage,
    override val diskord: Diskord
) : Event() {

    companion object {
        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): MessageUpdateEvent {
            val data = decoder.decodeFromJsonElement<UpdatedMessageData>(json)
            val message = UpdatedMessage(data, diskord)
            return MessageUpdateEvent(message, diskord)
        }
    }

    override suspend fun handle() {
        if (message.edited != null) call()
    }

}