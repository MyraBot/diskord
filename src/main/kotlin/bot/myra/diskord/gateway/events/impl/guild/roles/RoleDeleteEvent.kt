package bot.myra.diskord.gateway.events.impl.guild.roles

import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-role-delete)
 */
@Serializable
class RoleDeleteEvent(
    @SerialName("guild_id") val guildId: String,
    @SerialName("role_id") val roleId: String
) : Event()