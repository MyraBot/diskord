package bot.myra.diskord.gateway.events.impl.message.create

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.entities.message.MessageData
import bot.myra.diskord.gateway.events.impl.message.create.guild.GuildMessageCreateEventBroker
import bot.myra.diskord.gateway.events.types.EventAction
import bot.myra.diskord.gateway.events.types.EventBroker
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

class MessageCreateEventBroker(
    val message: Message,
    override val diskord: Diskord
) : EventBroker() {

    companion object {
        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): MessageCreateEventBroker {
            val data = decoder.decodeFromJsonElement<MessageData>(json)
            val message = Message(data, diskord)
            return MessageCreateEventBroker(message, diskord)
        }
    }

    override suspend fun choose(): EventAction = when (message.guildId.missing) {
        true  -> PrivateMessageCreateEvent(message, diskord)
        false -> GuildMessageCreateEventBroker(message, diskord)
    }

}