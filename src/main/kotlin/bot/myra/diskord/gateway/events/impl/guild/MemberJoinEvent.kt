package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.MemberData
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberJoinEvent(
    val member: Member,
    override val diskord: Diskord
) : Event() {

    companion object {
        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): MemberJoinEvent {
            val data = decoder.decodeFromJsonElement<MemberData>(json)
            val member = Member(data, diskord)
            return MemberJoinEvent(member, diskord)
        }
    }

    suspend fun getGuild() = diskord.getGuild(member.guildId)

}