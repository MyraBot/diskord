package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.GenericGuildCreateEvent

class GuildCachePolicy : GenericCachePolicy<String, Guild>() {

    @ListenTo(GenericGuildCreateEvent::class)
    fun onGuildCreate(event: GenericGuildCreateEvent) {
        Diskord.guildIds.add(event.guild.id)
        update(event.guild)
        Diskord.pendingGuilds[event.guild.id]?.complete(event.guild)
    }

}
