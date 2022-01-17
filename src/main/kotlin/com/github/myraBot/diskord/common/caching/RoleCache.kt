package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.Promise

object RoleCache : Cache<DoubleKey, Role>(
    retrieve = { key ->
        Promise.of(Endpoints.getRoles) {
            arg("guild.id", key.second)
        }.map { roles -> roles?.let { role -> role.first { it.id == key.first } } }
    }
)