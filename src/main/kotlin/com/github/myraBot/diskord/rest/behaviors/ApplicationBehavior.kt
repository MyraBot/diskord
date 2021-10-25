package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.rest.Endpoints

interface ApplicationBehavior {

    suspend fun getUser(id: String): User = User(Endpoints.getUser.execute { arg("user.id", id) })

}