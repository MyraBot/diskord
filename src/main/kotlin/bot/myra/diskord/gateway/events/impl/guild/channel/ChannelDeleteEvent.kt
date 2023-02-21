package bot.myra.diskord.gateway.events.impl.guild.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.channel.GenericChannel
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

data class ChannelDeleteEvent(
    val channel: GenericChannel,
    override val diskord: Diskord
) : Event() {

    companion object {
        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): ChannelDeleteEvent {
            val data = decoder.decodeFromJsonElement<ChannelData>(json)
            val channel = GenericChannel(data, diskord)
            return ChannelDeleteEvent(channel, diskord)
        }
    }

}