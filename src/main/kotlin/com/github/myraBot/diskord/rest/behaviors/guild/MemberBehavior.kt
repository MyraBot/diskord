package com.github.myraBot.diskord.rest.behaviors.guild

import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.behaviors.Entity
import com.github.myraBot.diskord.rest.request.Promise

interface MemberBehavior : Entity {

    val guildId: String

    suspend fun addRole(role: Role) = addRole(role.id)
    suspend fun addRole(id: String): Promise<Unit> {
        return Promise.of(Endpoints.addMemberRole) {
            arg("guild.id", guildId)
            arg("user.id", this@MemberBehavior.id)
            arg("role.id", id)
        }
    }

    suspend fun removeRole(role: Role) = removeRole(role.id)
    suspend fun removeRole(id: String): Promise<Unit> {
        return Promise.of(Endpoints.removeMemberRole) {
            arg("guild.id", guildId)
            arg("user.id", this@MemberBehavior.id)
            arg("role.id", id)
        }
    }

}