package diskord.gateway.listeners.impl.guild

import diskord.gateway.listeners.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-delete)
 *
 * @property id Guild id.
 */
data class GuildDeleteEvent(
        val id: String
) : Event()
