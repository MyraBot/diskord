package bot.myra.diskord.gateway.events.impl.guild.roles

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-role-update-guild-role-update-event-fields)
 */
class RoleUpdateEvent(
    val guildId: String,
    val role: Role,
    override val diskord: Diskord
) : Event() {

    companion object {
        @Serializable
        private data class Data(@SerialName("guild_id") val guildId: String, val role: Role)

        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): RoleUpdateEvent {
            val event = decoder.decodeFromJsonElement<Data>(json)
            return RoleUpdateEvent(event.guildId, event.role, diskord)
        }
    }

}