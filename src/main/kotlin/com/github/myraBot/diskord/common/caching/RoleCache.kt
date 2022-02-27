package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch

object RoleCache : Cache<DoubleKey, Role>(
    retrieve = { key ->
        val future = CompletableDeferred<Role>()
        RestClient.coroutineScope.launch {
            val roles = RestClient.executeAsync(Endpoints.getRoles) {
                arguments { arg("guild.id", key.first) }
            }.await()
            val role = roles.first { it.id == key.second }
            future.complete(role)
        }
        future
    }
)