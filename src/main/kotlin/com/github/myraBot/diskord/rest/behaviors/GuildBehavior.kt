package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.rest.Endpoints

interface GuildBehavior : Entity, GetTextChannelBehavior {
    suspend fun getMember(id: String): Member {
        val memberData = Endpoints.getGuildMember.execute {
            arg("guild.id", this@GuildBehavior.id)
            arg("user.id", id)
        }
        return Member.withUserInMember(memberData)
    }

    suspend fun getBotMember(): Member = getMember(Endpoints.getBotApplication.execute().id)
}