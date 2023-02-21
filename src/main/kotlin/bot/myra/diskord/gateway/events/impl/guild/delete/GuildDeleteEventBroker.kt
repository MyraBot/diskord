package bot.myra.diskord.gateway.events.impl.guild.delete

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.gateway.events.types.EventAction
import bot.myra.diskord.gateway.events.types.EventBroker
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

class GuildDeleteEventBroker(
    val guild: UnavailableGuild,
    val cachedGuild: Guild?,
    override val diskord: Diskord
) : EventBroker() {

    companion object {
        suspend fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): GuildDeleteEventBroker {
            val guild = decoder.decodeFromJsonElement<UnavailableGuild>(json)
            val cachedGuild = diskord.cachePolicy.guild
                .get(guild.id).value
                ?.let { Guild(diskord, it) }
            return GuildDeleteEventBroker(guild, cachedGuild, diskord)
        }
    }

    override suspend fun choose(): EventAction? {
        val shadowedGuild = diskord.unavailableGuilds.any { it.key == guild.id }
        if (shadowedGuild) return null // Guild got removed by trust and safety team

        return when (guild.gotKicked) {
            true  -> GuildLeaveEvent(guild, cachedGuild, diskord)
            false -> GuildUnloadEvent(guild, cachedGuild, diskord)
        }
    }

}
