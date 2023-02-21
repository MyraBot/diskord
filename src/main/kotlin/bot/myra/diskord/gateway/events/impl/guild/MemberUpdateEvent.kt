package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.MemberData
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

data class MemberUpdateEvent(
    val member: Member,
    override val diskord: Diskord
) : Event() {

    companion object {
        fun deserialize(json: JsonElement, deserializer: Json, diskord: Diskord): Event {
            val data = deserializer.decodeFromJsonElement<MemberData>(json)
            val member =  Member(data, diskord)
            return MemberUpdateEvent(member, diskord)
        }
    }

    suspend fun getGuild() = member.getGuild()

}
