package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.common.entities.user.UserData
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway-events#guild-member-remove)
 */
data class MemberRemoveEvent(
    val guildId: String,
    val user: User,
    override val diskord: Diskord
) : Event() {

    companion object {
        @Serializable
        private data class Data(@SerialName("guild_id") val guildId: String, val user: UserData)

        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): MemberRemoveEvent {
            val event = decoder.decodeFromJsonElement<Data>(json)
            val user = User(event.user, diskord)
            return MemberRemoveEvent(event.guildId, user, diskord)
        }
    }

}