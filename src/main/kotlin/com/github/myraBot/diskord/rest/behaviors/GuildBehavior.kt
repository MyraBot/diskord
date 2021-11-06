package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.rest.Endpoints

interface GuildBehavior : Entity, GetTextChannelBehavior {
    val guild: Guild

    suspend fun getMember(id: String): MemberData {
        return Endpoints.getGuildMember.execute {
            arg("guild.id", this@GuildBehavior.id)
            arg("user.id", id)
        }
    }

    suspend fun getBotMember(): MemberData = getMember(Endpoints.getBotApplication.execute().id)
}