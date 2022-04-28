@file:Suppress("unused")

package bot.myra.diskord.rest.behaviors.guild

import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.EntityProvider
import bot.myra.diskord.rest.behaviors.Entity
import bot.myra.diskord.rest.behaviors.GetTextChannelBehavior
import bot.myra.diskord.rest.request.RestClient

interface GuildBehavior : Entity, GetTextChannelBehavior {

    suspend fun getMember(id: String): Member? = EntityProvider.getMember(this.id, id)

    suspend fun getBotMember(): Member? = getMember(getApplication().id)

    suspend fun getMembers(limit: Int = 1000): List<Member> {
        val memberDataList = RestClient.execute(Endpoints.listGuildMembers) {
            arguments {
                arg("guild.id", this@GuildBehavior.id)
                arg("limit", limit)
            }
        }
        return memberDataList.map { Member.withUserInMember(it, id) }
    }

    suspend fun unbanMember(id: String, reason: String? = null): Unit {
        return RestClient.execute(Endpoints.removeGuildBan) {
            logReason = reason
            arguments {
                arg("guild.id", this@GuildBehavior.id)
                arg("user.id", id)
            }
        }
    }

    suspend fun getRoles(): List<Role> = RestClient.execute(Endpoints.getRoles) {
        arguments { arg("guild.id", this@GuildBehavior.id) }
    }

    suspend fun getRole(id: String): Role? = EntityProvider.getRole(this.id, id)

    suspend fun getChannels(): List<ChannelData> = EntityProvider.getGuildChannels(this.id)

}