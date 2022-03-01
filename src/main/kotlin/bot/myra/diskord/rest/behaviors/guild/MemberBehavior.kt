package bot.myra.diskord.rest.behaviors.guild

import bot.myra.diskord.common.entities.Role
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.behaviors.Entity
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

@Suppress("unused")
interface MemberBehavior : Entity {

    val guildId: String

    suspend fun addRoleAsync(role: Role) = addRoleAsync(role.id)

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