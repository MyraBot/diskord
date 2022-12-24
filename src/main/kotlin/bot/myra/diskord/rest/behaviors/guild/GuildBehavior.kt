@file:Suppress("unused")

package bot.myra.diskord.rest.behaviors.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.CdnEndpoints
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.EntityProvider
import bot.myra.diskord.rest.behaviors.Entity
import bot.myra.diskord.rest.behaviors.GetTextChannelBehavior
import bot.myra.diskord.rest.bodies.ModifyGuildRole
import bot.myra.diskord.rest.request.RestClient

interface GuildBehavior : Entity, GetTextChannelBehavior {
    val guild: Guild

    val icon: String? get() = guild.iconHash?.let { CdnEndpoints.guildIcon.apply { arg("guild_id", id); arg("guild_icon", it) } }
    val splash: String? get() = guild.splashHash?.let { CdnEndpoints.guildSplash.apply { arg("guild_id", id); arg("guild_splash", it) } }
    val discoverySplash: String? get() = guild.discoverySplashHash?.let { CdnEndpoints.guildDiscoverySplash.apply { arg("guild_id", id); arg("guild_discovery_splash", it) } }

    suspend fun getOwner() = getMember(guild.ownerId)
    suspend fun getMemberCount(): Int? = Diskord.fetchGuild(id).value?.approximateMemberCount?.value
    suspend fun getOnlineCount(): Int? = Diskord.fetchGuild(id).value?.approximateOnlineCount?.value
    fun getEmoji(name: String): Emoji? = guild.emojis.find { it.name == name }

    suspend fun getMember(id: String) = EntityProvider.getMember(this.id, id)
    suspend fun getBotMember() = getMember(getApplication().id)
    suspend fun getMembers(limit: Int = 1000) = EntityProvider.fetchGuildMembers(this@GuildBehavior.id, limit)

    suspend fun unbanMember(id: String, reason: String? = null) = RestClient.execute(Endpoints.removeGuildBan) {
        logReason = reason
        arguments {
            arg("guild.id", this@GuildBehavior.id)
            arg("user.id", id)
        }
    }

    suspend fun getRole(id: String): Role? = EntityProvider.getRole(this@GuildBehavior.id, id)
    suspend fun modifyRole(id: String, modify: ModifyGuildRole.() -> Unit) = RestClient.execute(Endpoints.modifyGuildRole) {
        json = ModifyGuildRole().apply(modify).toJson()
        arguments {
            arg("guild.id", this@GuildBehavior.id)
            arg("role.id", id)
        }
    }

    suspend fun getChannels(): List<ChannelData> = EntityProvider.getGuildChannels(this.id)

}