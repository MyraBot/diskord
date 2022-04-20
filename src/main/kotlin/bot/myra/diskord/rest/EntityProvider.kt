package bot.myra.diskord.rest

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.caching.DoubleKey
import bot.myra.diskord.common.entities.Role
import bot.myra.diskord.common.entities.User
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.rest.request.RestClient

object EntityProvider {

    suspend fun getUserNonNull(id: String): User =
        Diskord.cachePolicy.userCachePolicy.get(id)
            ?: RestClient.execute(Endpoints.getUser) {
                arguments { arg("user.id", id) }
            }

    suspend fun getUser(id: String): User? =
        Diskord.cachePolicy.userCachePolicy.get(id)
            ?: RestClient.executeNullable(Endpoints.getUser) {
                arguments { arg("user.id", id) }
            }

    suspend fun getGuild(id: String): Guild? = Diskord.cachePolicy.guildCachePolicy.get(id) ?: fetchGuild(id)
    suspend fun fetchGuild(id: String): Guild? = RestClient.executeNullable(Endpoints.getGuild) { arguments { arg("guild.id", id) } }

    suspend fun getGuildChannels(id: String): List<ChannelData> = Diskord.cachePolicy.channelCachePolicy.guildAssociation.view(id) ?: fetchGuildChannels(id)
    suspend fun fetchGuildChannels(id: String): List<ChannelData> = RestClient.execute(Endpoints.getChannels) { arguments { arg("guild.id", id) } }

    suspend fun getChannel(id: String): ChannelData? =
        Diskord.cachePolicy.channelCachePolicy.get(id)
            ?: RestClient.executeNullable(Endpoints.getChannel) {
                arguments { arg("channel.id", id) }
            }

    suspend fun getMember(guildId: String, userId: String): Member? {
        val key = DoubleKey(guildId, userId)
        return Diskord.cachePolicy.memberCachePolicy.get(key) ?: run {
            val member = RestClient.executeNullable(Endpoints.getGuildMember) {
                arguments {
                    arg("guild.id", guildId)
                    arg("user.id", userId)
                }
            }
            member?.let { Member.withUserInMember(member, guildId) }
        }
    }

    suspend fun getRole(guildId: String, roleId: String): Role? = Diskord.cachePolicy.roleCachePolicy.get(roleId) ?: run {
        val roles = RestClient.executeNullable(Endpoints.getRoles) {
            arguments { arg("guild.id", guildId) }
        }
        roles?.let { roles.first { it.id == roleId } }
    }

}