package bot.myra.diskord.common.caching.models

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.caching.GenericCachePolicy
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.GuildLoadEvent

class GuildCachePolicy : GenericCachePolicy<String, Guild>() {

    @ListenTo(GuildLoadEvent::class)
    fun onGuildCreate(event: GuildLoadEvent) {
        Diskord.guildIds.add(event.guild.id)
        update(event.guild)
        Diskord.pendingGuilds[event.guild.id]?.complete(event.guild)
    }

}
