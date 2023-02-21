package bot.myra.diskord.gateway.events.impl.guild.roles

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-role-delete)
 */
class RoleDeleteEvent(
    val roleId: String,
    val guildId: String,
    override val diskord: Diskord
) : Event() {

    companion object {
        @Serializable
        private data class Data(
            @SerialName("guild_id") val guildId: String,
            @SerialName("role_id") val roleId: String
        )

        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): RoleDeleteEvent {
            val event = decoder.decodeFromJsonElement<Data>(json)
            return RoleDeleteEvent(event.guildId, event.roleId, diskord)
        }
    }

}