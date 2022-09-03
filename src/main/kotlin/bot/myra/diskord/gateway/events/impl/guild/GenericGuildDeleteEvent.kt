package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.gateway.events.Event
import kotlinx.coroutines.runBlocking

data class GenericGuildDeleteEvent(
    val guild: UnavailableGuild,
    var cachedGuild: Guild? = runBlocking { Diskord.getGuild(guild.id) }
) : Event() {

    override suspend fun handle() {
        val shadowedGuild = Diskord.lazyLoadedGuilds.none { it == guild.id }
        if (shadowedGuild) return // Guild got removed by trust and safety team

        when (guild.gotKicked) {
            true  -> GuildLeaveEvent(guild, cachedGuild)
            false -> GuildUnloadEvent(guild, cachedGuild)
        }.call()
    }

}
