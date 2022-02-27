package com.github.myraBot.diskord.rest.behaviors.guild

import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.behaviors.Entity
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

interface MemberBehavior : Entity {

    val guildId: String

    suspend fun addRole(role: Role) = addRoleAsync(role.id)

    suspend fun addRoleAsync(id: String): Deferred<Unit> {
        return RestClient.executeAsync(Endpoints.addMemberRole) {
            arguments {
                arg("guild.id", guildId)
                arg("user.id", this@MemberBehavior.id)
                arg("role.id", id)
            }
        }
    }

    suspend fun removeRoleAsync(role: Role) = removeRoleAsync(role.id)

    suspend fun removeRoleAsync(id: String): Deferred<Unit> {
        return RestClient.executeAsync(Endpoints.removeMemberRole) {
            arguments {
                arg("guild.id", guildId)
                arg("user.id", this@MemberBehavior.id)
                arg("role.id", id)
            }
        }
    }

}