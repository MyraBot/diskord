package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.ExtendedGuild
import bot.myra.diskord.gateway.events.types.EventAction
import bot.myra.diskord.gateway.events.types.EventBroker

class GuildCreateEventBroker(
    val guild: ExtendedGuild
) : EventBroker() {

    override suspend fun choose(): EventAction {
        Diskord.unavailableGuilds.remove(guild.id)?.complete(guild.toGuild())
        return when (guild.id in Diskord.unavailableGuilds && guild.available) {
            true  -> GuildAvailableEvent(guild)
            false -> GuildCreateEvent(guild)
        }
    }

}