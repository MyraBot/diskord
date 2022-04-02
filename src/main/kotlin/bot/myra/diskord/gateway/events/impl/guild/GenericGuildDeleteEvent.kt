package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.gateway.events.Event

data class GenericGuildDeleteEvent(
    val guild: UnavailableGuild
) : Event() {

    override suspend fun prepareEvent() {
        if (guild.wasLeft) GuildLeaveEvent(guild)
        else GuildUnloadEvent(guild)
    }

}
