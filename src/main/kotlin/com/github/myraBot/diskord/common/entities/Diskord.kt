package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.rest.Endpoints

object Diskord {

    suspend fun getUser(id: String): User = User(Endpoints.getUser.execute { arg("user.id", id) })

}