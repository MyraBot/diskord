package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.rest.Endpoints

interface ApplicationBehavior {

    suspend fun getUser(id: String) = Endpoints.getUser.execute { arg("user.id", id) }

}