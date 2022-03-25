package bot.myra.diskord.rest

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.caching.DoubleKey
import bot.myra.diskord.common.entities.Role
import bot.myra.diskord.common.entities.User
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

object EntityProvider {

    fun getUserNonNull(id: String): Deferred<User> =
        Diskord.cachePolicy.userCachePolicy.get(id)?.let {
            CompletableDeferred(it)
        } ?: RestClient.executeAsync(Endpoints.getUser) {
            arguments {
                arg("user.id", id)
            }
        }

    fun getUser(id: String): Deferred<User?> =
        Diskord.cachePolicy.userCachePolicy.get(id)?.let {
            CompletableDeferred(it)
        } ?: RestClient.executeNullableAsync(Endpoints.getUser) {
            arguments {
                arg("user.id", id)
            }
        }

    fun getGuild(id: String): Deferred<Guild?> =
        Diskord.cachePolicy.guildCachePolicy.get(id)?.let {
            CompletableDeferred(it)
        } ?: RestClient.executeNullableAsync(Endpoints.getGuild) {
            arguments {
                arg("guild.id", id)
            }
        }

    fun getChannel(id: String): Deferred<ChannelData?> =
        Diskord.cachePolicy.channelCachePolicy.get(id)?.let {
            CompletableDeferred(it)
        } ?: RestClient.executeNullableAsync(Endpoints.getChannel) {
            arguments {
                arg("channel.id", id)
            }
        }

    fun getMember(guildId: String, userId: String): Deferred<Member?> {
        val key = DoubleKey(guildId, userId)
        return Diskord.cachePolicy.memberCachePolicy.get(key)?.let {
            CompletableDeferred(it)
        } ?: run {
            val future = CompletableDeferred<Member?>()
            RestClient.coroutineScope.launch {
                val data = RestClient.executeNullableAsync(Endpoints.getGuildMember) {
                    arguments {
                        arg("guild.id", guildId)
                        arg("user.id", userId)
                    }
                }.await()
                if (data == null) future.complete(value = null)
                else future.complete(Member.withUserInMember(data, guildId))
            }
            return future
        }
    }

    fun getRole(guildId: String, roleId: String): Deferred<Role?> {
        return Diskord.cachePolicy.roleCachePolicy.get(roleId)?.let {
            CompletableDeferred(it)
        } ?: run {
            val future = CompletableDeferred<Role?>()
            RestClient.coroutineScope.launch {
                val roles = RestClient.executeNullableAsync(Endpoints.getRoles) {
                    arguments {
                        arg("guild.id", guildId)
                    }
                }.await()

                if (roles == null) future.complete(value = null)
                else future.complete(roles.first { it.id == roleId })
            }
            return future
        }
    }

}