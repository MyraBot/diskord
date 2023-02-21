
package bot.myra.diskord.rest.behaviors.guild

import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.channel.GenericChannel
import bot.myra.diskord.common.entities.guild.GuildData
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.behaviors.Entity
import bot.myra.diskord.rest.bodies.ModifyGuildRole
import bot.myra.diskord.rest.getChannel
import bot.myra.diskord.rest.request.RestClient
import bot.myra.diskord.rest.request.Result

@Suppress("unused")
interface GuildBehavior : Entity {
    val guildData: GuildData

    suspend fun getOwner() = getMember(guildData.ownerId)
    suspend fun getMemberCount(): Int? = diskord.fetchGuild(id).value?.approximateMemberCount?.value
    suspend fun getOnlineCount(): Int? = diskord.fetchGuild(id).value?.approximateOnlineCount?.value
    fun getEmoji(name: String): Emoji? = guildData.emojis.find { it.name == name }

    suspend fun getMember(id: String) = diskord.getMember(this.id, id)
    suspend fun getBotMember() = getMember(getApplication().id)
    suspend fun getMembers(limit: Int = 1000) = diskord.fetchGuildMembers(this@GuildBehavior.id, limit)

    suspend fun unbanMember(id: String, reason: String? = null) = diskord.rest.execute(Endpoints.removeGuildBan) {
        logReason = reason
        arguments {
            arg("guild.id", this@GuildBehavior.id)
            arg("user.id", id)
        }
    }

    suspend fun getRole(id: String): Role? = diskord.getRole(this@GuildBehavior.id, id)
    suspend fun modifyRole(id: String, modify: ModifyGuildRole.() -> Unit) = diskord.rest.execute(Endpoints.modifyGuildRole) {
        json = ModifyGuildRole().apply(modify).toJson()
        arguments {
            arg("guild.id", this@GuildBehavior.id)
            arg("role.id", id)
        }
    }

    suspend fun getChannels(): List<GenericChannel> = diskord.getGuildChannels(this.id)
}

suspend inline fun <reified T> GuildBehavior.getChannel(id: String): Result<T> = diskord.getChannel<T>(id)