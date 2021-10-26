package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entityData.MemberData
import com.github.myraBot.diskord.rest.Endpoints

interface GuildBehavior : Entity, GetTextChannelBehavior {
    suspend fun getMember(id: String) = Endpoints.getGuildMember.execute {arg("guild.id", this@GuildBehavior.id); arg("user.id", id) }
    suspend fun botMember(): MemberData = getMember(Endpoints.getBotApplication.execute().id)
}