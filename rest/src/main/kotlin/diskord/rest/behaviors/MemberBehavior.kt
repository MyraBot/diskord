package diskord.rest.behaviors

import diskord.common.entities.Role
import diskord.rest.Endpoints

interface MemberBehavior : Entity {

    val guildId: String

    suspend fun addRole(role: Role) = addRole(role.id)
    suspend fun addRole(id: String) {
        Endpoints.addMemberRole.execute {
            arg("guild.id", guildId)
            arg("user.id", this@MemberBehavior.id)
            arg("role.id", id)
        }
    }

    suspend fun removeRole(role: Role) = removeRole(role.id)
    suspend fun removeRole(id: String) {
        Endpoints.removeMemberRole.execute {
            arg("guild.id", guildId)
            arg("user.id", this@MemberBehavior.id)
            arg("role.id", id)
        }
    }

}