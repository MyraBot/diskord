package com.github.myraBot.diskord.rest.behaviors.guild

import com.github.myraBot.diskord.common.caching.DoubleKey
import com.github.myraBot.diskord.common.caching.MemberCache
import com.github.myraBot.diskord.common.caching.RoleCache
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.behaviors.Entity
import com.github.myraBot.diskord.rest.behaviors.GetTextChannelBehavior
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

interface GuildBehavior : Entity, GetTextChannelBehavior {

    suspend fun getMemberAsync(id: String): Deferred<Member?> = MemberCache.get(DoubleKey(this.id, id))

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

    suspend fun getRoleAsync(id: String): Deferred<Role?> = RoleCache.get(DoubleKey(this.id, id))

    suspend fun getChannelsAsync(): Deferred<List<ChannelData>> = RestClient.executeAsync(Endpoints.getChannels) {
        arguments { arg("guild.id", this@GuildBehavior.id) }
    }

}