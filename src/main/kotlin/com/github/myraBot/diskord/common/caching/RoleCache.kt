package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.rest.Endpoints
import kotlinx.coroutines.runBlocking

object RoleCache : DoubleKeyCache<String, String, Role>() {

    override fun retrieve(firstKey: String, secondKey: String): Role? {
        return runBlocking {
            Endpoints.getRoles.execute { arg("guild.id", firstKey) }
        }?.first { it.id == secondKey }
    }

    override fun update(firstKey: String, secondKey: String, value: Role) {
        map[Key(firstKey, secondKey)] = value
    }

}