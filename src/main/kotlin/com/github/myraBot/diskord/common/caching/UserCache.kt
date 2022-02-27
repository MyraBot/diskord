package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

object UserCache : Cache<String, User>() {

    override fun retrieveAsync(key: String): Deferred<User?> {
        return RestClient.executeNullableAsync(Endpoints.getUser) {
            arguments { arg("user.id", key) }
        }
    }

}

