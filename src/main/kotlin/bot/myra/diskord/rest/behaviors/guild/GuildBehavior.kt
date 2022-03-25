@file:Suppress("unused")

package bot.myra.diskord.rest.behaviors.guild

import bot.myra.diskord.common.entities.Role
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.EntityProvider
import bot.myra.diskord.rest.behaviors.Entity
import bot.myra.diskord.rest.behaviors.GetTextChannelBehavior
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

interface GuildBehavior : Entity, GetTextChannelBehavior {

    suspend fun getMemberAsync(id: String): Deferred<Member?> = EntityProvider.getMember(this.id, id)

    suspend fun getBotMemberAsync(): Deferred<Member?> {
        val future = CompletableDeferred<Member?>()
        RestClient.coroutineScope.launch {
            val application = getApplicationAsync().await()
            val member = getMemberAsync(application.id).await()
            future.complete(member)
        }
        return future
    }

    suspend fun getMembersAsync(limit: Int = 1000): Deferred<List<Member>> {
        val future = CompletableDeferred<List<Member>>()
        RestClient.coroutineScope.launch {
            val memberDataList = RestClient.executeAsync(Endpoints.listGuildMembers) {
                arguments {
                    arg("guild.id", this@GuildBehavior.id)
                    arg("limit", limit)
                }
            }.await()
            val members = memberDataList.map { Member.withUserInMember(it, id) }
            future.complete(members)
        }
        return future
    }

    suspend fun unbanMemberAsync(id: String, reason: String? = null): Deferred<Unit> {
        return RestClient.executeAsync(Endpoints.removeGuildBan) {
            logReason = reason
            arguments {
                arg("guild.id", this@GuildBehavior.id)
                arg("user.id", id)
            }
        }
    }

    suspend fun getRolesAsync(): Deferred<List<Role>> = RestClient.executeAsync(Endpoints.getRoles) {
        arguments { arg("guild.id", this@GuildBehavior.id) }
    }

    suspend fun getRoleAsync(id: String): Deferred<Role?> = EntityProvider.getRole(this.id, id)

    suspend fun getChannelsAsync(): Deferred<List<ChannelData>> = RestClient.executeAsync(Endpoints.getChannels) {
        arguments { arg("guild.id", this@GuildBehavior.id) }
    }

}