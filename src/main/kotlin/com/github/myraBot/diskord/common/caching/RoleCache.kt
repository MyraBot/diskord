package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

object RoleCache : Cache<DoubleKey, Role>() {

    override fun retrieveAsync(key: DoubleKey): Deferred<Role?> {
        val future = CompletableDeferred<Role?>()
        RestClient.coroutineScope.launch {
            val roles = RestClient.executeNullableAsync(Endpoints.getRoles) {
                arguments { arg("guild.id", key.first) }
            }.await() ?: return@launch Unit.also { future.complete(null) }
            val role = roles.first { it.id == key.second }
            future.complete(role)
        }
        return future
    }

}