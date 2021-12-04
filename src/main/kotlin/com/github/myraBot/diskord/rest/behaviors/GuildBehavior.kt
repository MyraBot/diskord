package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.caching.RoleCache
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.rest.Endpoints

interface GuildBehavior : Entity, GetTextChannelBehavior {

    suspend fun getMember(id: String): Member? {
        val memberData = Endpoints.getGuildMember.execute {
            arg("guild.id", this@GuildBehavior.id)
            arg("user.id", id)
        }
        return memberData?.let { Member.withUserInMember(it, this.id) }
    }

    suspend fun getBotMember(): Member = getMember(Endpoints.getBotApplication.executeNonNull().id)!!
    suspend fun getRoles(): List<Role> = Endpoints.getRoles.executeNonNull { arg("guild.id", this@GuildBehavior.id) }
    suspend fun getRole(id: String): Role? = RoleCache[this.id, id]
}