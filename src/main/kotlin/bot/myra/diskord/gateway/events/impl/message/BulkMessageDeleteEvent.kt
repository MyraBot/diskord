package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.message.AnonymousMessages
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

open class BulkMessageDeleteEvent(
    val message: AnonymousMessages,
    override val diskord: Diskord
) : Event() {

    companion object {
        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): BulkMessageDeleteEvent {
            val anonymousMessages = decoder.decodeFromJsonElement<AnonymousMessages>(json)
            return BulkMessageDeleteEvent(anonymousMessages, diskord)
        }
    }

}