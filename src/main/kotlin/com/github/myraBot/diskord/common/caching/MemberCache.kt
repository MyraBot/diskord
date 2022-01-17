package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.Promise

object MemberCache : Cache<DoubleKey, Member>(
    retrieve = { key ->
        Promise.of(Endpoints.getGuildMember) {
            arg("user.id", key.first)
            arg("guild.id", key.second)
        }.map { data -> data?.let { Member.withUserInMember(it, key.second) } }
    }
)

