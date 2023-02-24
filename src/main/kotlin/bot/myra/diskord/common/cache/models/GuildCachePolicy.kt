package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.entities.guild.GuildData
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.create.GenericGuildCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.delete.GuildLeaveEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleUpdateEvent
import bot.myra.diskord.gateway.events.types.Event

class MutableGuildCachePolicy : GuildCachePolicy() {

    private fun checkIntents(event: Event) {
        if (GatewayIntent.GUILDS !in event.diskord.intents) {
            throw MissingIntentException(GuildCachePolicy::class, GatewayIntent.GUILDS)
        }
    }

    @ListenTo(GenericGuildCreateEvent::class)
    suspend fun onGuildCreate(event: GenericGuildCreateEvent) {
        checkIntents(event)

        event.guild.asExtendedGuild()?.also {
            update(it.guildData)
        }
    }

    @ListenTo(GuildLeaveEvent::class)
    suspend fun onGuildLeave(event: GuildLeaveEvent) {
        checkIntents(event)

        event.diskord.guildIds.remove(event.guild.id)
        remove(event.guild.id)
    }

    @ListenTo(RoleCreateEvent::class)
    suspend fun onRoleCreate(event: RoleCreateEvent) {
        checkIntents(event)
        get(event.guildId).value?.apply {
            roles = roles.toMutableList().apply { add(event.role) }
        }
    }

    @ListenTo(RoleUpdateEvent::class)
    suspend fun onRoleUpdate(event: RoleUpdateEvent) {
        checkIntents(event)
        get(event.guildId).value?.apply {
            roles = roles.toMutableList().apply {
                removeIf { it.id == event.role.id }
                add(event.role)
            }
        }
    }

    @ListenTo(RoleDeleteEvent::class)
    suspend fun onRoleDelete(event: RoleDeleteEvent) {
        checkIntents(event)
        get(event.guildId).value?.apply {
            roles = roles.toMutableList().apply { removeIf { it.id == event.roleId } }
        }
    }

}

class DisabledGuildCachePolicy : GuildCachePolicy()

abstract class GuildCachePolicy : GenericCachePolicy<String, GuildData>() {
    override fun getAsKey(value: GuildData): String = value.id
}