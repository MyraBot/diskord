package bot.myra.diskord.common.caching

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.GuildLoadEvent
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

object GuildCache : Cache<String, Guild>() {

    override fun retrieveAsync(key: String): Deferred<Guild?> {
        return RestClient.executeAsync(Endpoints.getGuild) {
            arguments { arg("guild.id", key) }
        }
    }

    @ListenTo(GuildLoadEvent::class)
    fun onGuildCreate(event: GuildLoadEvent) {
        Diskord.guildIds.add(event.guild.id)
        cache[event.guild.id] = event.guild
        Diskord.pendingGuilds[event.guild.id]?.complete(event.guild)
    }

}

