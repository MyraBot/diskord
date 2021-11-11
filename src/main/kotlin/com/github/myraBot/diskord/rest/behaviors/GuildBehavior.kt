package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.SimpleGuild
import com.github.myraBot.diskord.rest.Endpoints

interface GuildBehavior : Entity, GetTextChannelBehavior {

    suspend fun getMember(id: String): Member {
        val memberData = Endpoints.getGuildMember.execute {
            arg("guild.id", this@GuildBehavior.id)
            arg("user.id", id)
        }
        return Member.withUserInMember(memberData, SimpleGuild(this.id))
    }

    suspend fun getBotMember(): Member = getMember(Endpoints.getBotApplication.execute().id)

    suspend fun getRoles(): List<Role> = Endpoints.getRoles.execute { arg("guild.id", id) }

}