package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.DetailedGuild
import bot.myra.diskord.gateway.events.types.EventBroker

class GuildCreateEventBroker(
    val guild: DetailedGuild
) : EventBroker() {

    override suspend fun choose() = when (guild.id in Diskord.unavailableGuilds && guild.available) {
        true  -> {
            Diskord.unavailableGuilds.remove(guild.id)
            GuildAvailableEvent(guild)
        }
        false -> GuildCreateEvent(guild)
    }

}