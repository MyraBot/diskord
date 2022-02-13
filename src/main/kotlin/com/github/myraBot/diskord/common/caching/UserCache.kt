package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.promises.Promise

object UserCache : Cache<String, User>(
    retrieve = { key ->
        Promise.of(Endpoints.getUser) {
            arguments { arg("user.id", key) }
        }
    }
)

