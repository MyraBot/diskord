package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.rest.Endpoints
import kotlinx.serialization.Serializable

@Serializable
data class Application(
        val id: String
) {
    suspend fun getUser(id: String) = Endpoints.getUser.execute { arg("user.id", id) }
}
