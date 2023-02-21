package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.message.AnonymousMessage
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

open class MessageDeleteEvent(
    val message: AnonymousMessage,
    override val diskord: Diskord
) : Event() {

    companion object {
        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): MessageDeleteEvent {
            val anonymousMessage = decoder.decodeFromJsonElement<AnonymousMessage>(json)
            return MessageDeleteEvent(anonymousMessage, diskord)
        }
    }

}