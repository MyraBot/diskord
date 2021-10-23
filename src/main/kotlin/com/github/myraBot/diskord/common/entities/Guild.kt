package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.rest.Endpoints
import kotlinx.serialization.Serializable

@Serializable
data class Guild(
        val id: String
) {
    suspend fun getMember(id: String) = Endpoints.getGuildMember.execute { arg("user.id", id) }
    suspend fun botMember(): Member = getMember(Endpoints.getBotApplication.execute().id)
}
