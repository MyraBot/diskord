package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.GenericGuildCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleUpdateEvent

class MutableGuildCachePolicy : GuildCachePolicy() {
    init {
        if (GatewayIntent.GUILDS !in Diskord.intents) {
            throw MissingIntentException(Diskord.cachePolicy.guild, GatewayIntent.GUILDS)
        }
    }
}

class DisabledGuildCachePolicy : GuildCachePolicy()

open class GuildCachePolicy : GenericCachePolicy<String, Guild>() {

    @ListenTo(GenericGuildCreateEvent::class)
    fun onGuildCreate(event: GenericGuildCreateEvent) {
        Diskord.guildIds.add(event.guild.id)
        update(event.guild)
        Diskord.pendingGuilds[event.guild.id]?.complete(event.guild)
    }

    @ListenTo(RoleCreateEvent::class)
    fun onRoleCreate(event: RoleCreateEvent) = get(event.guildId)?.apply {
        roles = roles.toMutableList().apply { add(event.role) }
    }

    @ListenTo(RoleUpdateEvent::class)
    fun onRoleUpdate(event: RoleUpdateEvent) = get(event.guildId)?.apply {
        roles = roles.toMutableList().apply {
            removeIf { it.id == event.role.id }
            add(event.role)
        }
    }

    @ListenTo(RoleDeleteEvent::class)
    fun onRoleDelete(event: RoleDeleteEvent) = get(event.guildId)?.apply {
        roles = roles.toMutableList().apply { removeIf { it.id == event.roleId } }
    }

}
