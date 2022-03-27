package bot.myra.diskord.rest.behaviors.guild

import bot.myra.diskord.common.entities.Role
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.behaviors.Entity
import bot.myra.diskord.rest.request.RestClient

@Suppress("unused")
interface MemberBehavior : Entity {

    val guildId: String

    suspend fun addRole(role: Role) = addRole(role.id)

    suspend fun addRole(id: String):Unit {
        return RestClient.execute(Endpoints.addMemberRole) {
            arguments {
                arg("guild.id", guildId)
                arg("user.id", this@MemberBehavior.id)
                arg("role.id", id)
            }
        }
    }

    suspend fun removeRole(role: Role) = removeRole(role.id)

    suspend fun removeRole(id: String):Unit {
        return RestClient.execute(Endpoints.removeMemberRole) {
            arguments {
                arg("guild.id", guildId)
                arg("user.id", this@MemberBehavior.id)
                arg("role.id", id)
            }
        }
    }

}