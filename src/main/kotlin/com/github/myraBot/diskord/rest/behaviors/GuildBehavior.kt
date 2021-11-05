package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.Member
import com.github.myraBot.diskord.common.entityData.GuildData
import com.github.myraBot.diskord.rest.Endpoints

interface GuildBehavior : Entity, GetTextChannelBehavior {
    val guildData: GuildData

    suspend fun getMember(id: String): Member {
        val memberData = Endpoints.getGuildMember.execute {
            arg("guild.id", this@GuildBehavior.id)
            arg("user.id", id)
        }
        return Member(guildData.id, memberData)
    }

    suspend fun getBotMember(): Member = getMember(Endpoints.getBotApplication.execute().id)
}