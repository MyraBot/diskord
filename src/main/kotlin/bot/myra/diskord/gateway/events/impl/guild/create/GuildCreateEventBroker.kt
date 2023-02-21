package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.PossibleUnavailableGuild
import bot.myra.diskord.gateway.events.types.EventAction
import bot.myra.diskord.gateway.events.types.EventBroker
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class GuildCreateEventBroker(
    val guild: PossibleUnavailableGuild,
    private val isJoin: Boolean,
    override val diskord: Diskord
) : EventBroker() {

    companion object {
        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): GuildCreateEventBroker {
            val guild = PossibleUnavailableGuild.deserialize(json, decoder, diskord)
            val isJoin = guild.id !in diskord.guildIds
            val event = GuildCreateEventBroker(guild, isJoin, diskord)

            diskord.guildIds.add(guild.id)
            return event
        }
    }

    override suspend fun choose(): EventAction = when (isJoin) {
        true  -> GuildCreateEvent(guild, diskord)
        false -> GuildLazyLoadEvent(guild, diskord)
    }

}