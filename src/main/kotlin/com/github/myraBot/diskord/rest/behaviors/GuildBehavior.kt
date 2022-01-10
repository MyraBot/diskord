package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.caching.MemberCache
import com.github.myraBot.diskord.common.caching.RoleCache
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.rest.Endpoints

interface GuildBehavior : Entity, GetTextChannelBehavior {

    suspend fun getMember(id: String): Member? = MemberCache[this.id, id]
    suspend fun getBotMember(): Member = getMember(Endpoints.getBotApplication.executeNonNull().id)!!
    suspend fun getMembers(limit: Int = 1000) = Endpoints.listGuildMembers.executeNonNull {
        arg("guild.id", this@GuildBehavior.id)
        arg("limit", limit)
    }
        .map { Member.withUserInMember(it, this.id) }
        .onEach { MemberCache.update(this.id, it.id, it) }

    suspend fun getRoles(): List<Role> = Endpoints.getRoles.executeNonNull { arg("guild.id", this@GuildBehavior.id) }
    suspend fun getRole(id: String): Role? = RoleCache[this.id, id]
    suspend fun getChannels(): List<ChannelData> = Endpoints.getChannels.executeNonNull { arg("guild.id", this@GuildBehavior.id) }
}