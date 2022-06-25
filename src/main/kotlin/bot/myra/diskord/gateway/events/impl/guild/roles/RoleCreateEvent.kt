package bot.myra.diskord.gateway.events.impl.guild.roles

import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.gateway.events.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-role-create-guild-role-create-event-fields)
 */
@Serializable
class RoleCreateEvent(
    @SerialName("guild_id") val guildId: String,
    val role: Role
) : Event()