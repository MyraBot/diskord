package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.rest.Endpoints
import kotlinx.coroutines.runBlocking

object RoleCache : DoubleKeyCache<String, String, Role>() {

    override fun retrieve(firstKey: String, secondKey: String): Role? {
        return map.getOrElse(Key(firstKey, secondKey)) {
            runBlocking {
                val roles = Endpoints.getRoles.execute { arg("guild.id", firstKey) }
                roles?.forEach { map[Key(firstKey, it.id)] = it }
                map[Key(firstKey, secondKey)]
            }
        }
    }

    override fun update(firstKey: String, secondKey: String, value: Role) {
        map[Key(firstKey, secondKey)] = value
    }

}