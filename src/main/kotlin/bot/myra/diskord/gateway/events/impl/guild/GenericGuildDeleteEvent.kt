package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.gateway.events.Event

data class GenericGuildDeleteEvent(
    val guild: UnavailableGuild
) : Event() {

    override fun prepareEvent() {
        val shadowedGuild = Diskord.lazyLoadedGuilds.none { it == guild.id }
        if (shadowedGuild) return // Guild got removed by trust and safety team

        when (guild.gotKicked) {
            true  -> GuildLeaveEvent(guild)
            false -> GuildUnloadEvent(guild)
        }.call()
    }

}
