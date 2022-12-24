package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.create.GenericGuildCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.delete.GuildLeaveEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleUpdateEvent

class MutableGuildCachePolicy : GuildCachePolicy() {

    init {
        if (GatewayIntent.GUILDS !in Diskord.intents) throw MissingIntentException(GuildCachePolicy::class, GatewayIntent.GUILDS)
    }

    @ListenTo(GenericGuildCreateEvent::class)
    suspend fun onGuildCreate(event: GenericGuildCreateEvent) {
        val guild = event.guild.toGuild()
        Diskord.guildIds.add(event.guild.id)
        update(guild)

        Diskord.lazyLoadedGuilds.add(event.guild.id)
        Diskord.pendingGuilds[event.guild.id]?.complete(guild)
    }

    @ListenTo(GuildLeaveEvent::class)
    suspend fun onGuildLeave(event: GuildLeaveEvent) {
        Diskord.guildIds.remove(event.guild.id)
        remove(event.guild.id)
    }

    @ListenTo(RoleCreateEvent::class)
    suspend fun onRoleCreate(event: RoleCreateEvent) = get(event.guildId).value?.apply {
        roles = roles.toMutableList().apply { add(event.role) }
    }

    @ListenTo(RoleUpdateEvent::class)
    suspend fun onRoleUpdate(event: RoleUpdateEvent) = get(event.guildId).value?.apply {
        roles = roles.toMutableList().apply {
            removeIf { it.id == event.role.id }
            add(event.role)
        }
    }

    @ListenTo(RoleDeleteEvent::class)
    suspend fun onRoleDelete(event: RoleDeleteEvent) = get(event.guildId).value?.apply {
        roles = roles.toMutableList().apply { removeIf { it.id == event.roleId } }
    }

}

class DisabledGuildCachePolicy : GuildCachePolicy()

abstract class GuildCachePolicy : GenericCachePolicy<String, Guild>() {
    override fun getAsKey(value: Guild): String = value.id
}