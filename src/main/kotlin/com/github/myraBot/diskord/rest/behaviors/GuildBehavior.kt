package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entityData.MemberData
import com.github.myraBot.diskord.rest.Endpoints

interface GuildBehavior : GetTextChannelBehavior {
    suspend fun getMember(id: String) = Endpoints.getGuildMember.execute { arg("user.id", id) }
    suspend fun botMember(): MemberData = getMember(Endpoints.getBotApplication.execute().id)
}