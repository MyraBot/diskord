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
import com.github.myraBot.diskord.rest.request.promises.Promise

interface GuildBehavior : Entity, GetTextChannelBehavior {

    suspend fun getMember(id: String): Promise<Member> = MemberCache.get(DoubleKey(this.id, id))

    suspend fun getBotMember(): Promise<Member> = getApplication().then { getMember(it!!.id) }

    suspend fun getMembers(limit: Int = 1000): Promise<List<Member>> = Promise.of(Endpoints.listGuildMembers) {
        arguments {
            arg("guild.id", this@GuildBehavior.id)
            arg("limit", limit)
        }
    }.map { members ->
        members?.map { Member.withUserInMember(it, this.id) }
    }

    suspend fun unbanMember(id: String, reason: String? = null): Promise<Unit> {
        return Promise.of(Endpoints.removeGuildBan) {
            logReason = reason
            arguments {
                arg("guild.id", this@GuildBehavior.id)
                arg("user.id", id)
            }
        }
    }

    suspend fun getRoles(): Promise<List<Role>> = Promise.of(Endpoints.getRoles) {
        arguments { arg("guild.id", this@GuildBehavior.id) }
    }

    suspend fun getRole(id: String): Promise<Role> = RoleCache.get(DoubleKey(this.id, id))

    suspend fun getChannels(): Promise<List<ChannelData>> = Promise.of(Endpoints.getChannels) {
        arguments { arg("guild.id", this@GuildBehavior.id) }
    }

}