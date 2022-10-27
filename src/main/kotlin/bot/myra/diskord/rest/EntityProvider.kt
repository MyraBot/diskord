package bot.myra.diskord.rest

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.MemberCacheKey
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommand
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.GenericGuild
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.rest.request.RestClient

object EntityProvider {

    suspend fun getApplicationCommands(): List<SlashCommand> =
        RestClient.execute(Endpoints.getGlobalApplicationCommands) {
            arguments { arg("application.id", Diskord.id) }
        }

    suspend fun getUserNonNull(id: String): User =
        Diskord.cachePolicy.user.get(id)
            ?: RestClient.execute(Endpoints.getUser) {
                arguments { arg("user.id", id) }
            }.also { Diskord.cachePolicy.user.update(it) }

    suspend fun getUser(id: String): User? =
        Diskord.cachePolicy.user.get(id)
            ?: RestClient.executeNullable(Endpoints.getUser) {
                arguments { arg("user.id", id) }
            }?.also { Diskord.cachePolicy.user.update(it) }

    suspend fun fetchMessages(channelId: String, max: Int = 100, before: String? = null, after: String? = null): List<Message> {
        if (before != null && after != null) throw IllegalArgumentException("Only one, before or after can be set")
        return RestClient.execute(Endpoints.getChannelMessages) {
            arguments { arg("channel.id", channelId) }
            queryParameter.add("limit" to max)
            before?.let { queryParameter.add("before" to before) }
            after?.let { queryParameter.add("after" to after) }
        }
    }

    suspend fun getMessage(channelId: String, messageId: String) =
        RestClient.executeNullable(Endpoints.getChannelMessage) {
            arguments {
                arg("channel.id", channelId)
                arg("message.id", messageId)
            }
        }

    suspend fun getGuild(id: String): GenericGuild? = Diskord.cachePolicy.guild.get(id) ?: fetchGuild(id)

    suspend fun fetchGuild(id: String): Guild? = RestClient.executeNullable(Endpoints.getGuild) {
            arguments { arg("guild.id", id) }
        }?.also { Diskord.cachePolicy.guild.update(it) }

    suspend fun getGuildChannels(id: String): List<ChannelData> = Diskord.cachePolicy.channel.viewByGuild(id) ?: fetchGuildChannels(id)

    suspend fun fetchGuildChannels(id: String): List<ChannelData> =
        RestClient.execute(Endpoints.getChannels) {
            arguments { arg("guild.id", id) }
        }.onEach { Diskord.cachePolicy.channel.update(it) }

    suspend fun getChannel(id: String): ChannelData? =
        Diskord.cachePolicy.channel.get(id)
            ?: RestClient.executeNullable(Endpoints.getChannel) {
                arguments { arg("channel.id", id) }
            }?.also { Diskord.cachePolicy.channel.update(it) }

    suspend fun getMember(guildId: String, userId: String): Member? {
        val key = MemberCacheKey(guildId, userId)
        return Diskord.cachePolicy.member.get(key)
            ?: RestClient.executeNullable(Endpoints.getGuildMember) {
                arguments {
                    arg("guild.id", guildId)
                    arg("user.id", userId)
                }
            }?.let { Member.withUserInMember(it, guildId) }
                ?.also { Diskord.cachePolicy.member.update(it) }
    }

    suspend fun fetchGuildMembers(guildId: String, limit: Int = 100) =
        RestClient.execute(Endpoints.listGuildMembers) {
            arguments {
                arg("guild.id", guildId)
                arg("limit", limit)
            }
        }
            .map { Member.withUserInMember(it, guildId) }
            .onEach { Diskord.cachePolicy.member.update(it) }

    suspend fun fetchRoles(guildId: String): List<Role>? = Diskord.cachePolicy.guild.get(guildId)?.roles ?: RestClient.executeNullable(Endpoints.getGuild) {
        arguments { arg("guild.id", guildId) }
    }?.also { Diskord.cachePolicy.guild.update(it) }?.roles

    suspend fun getRole(guildId: String, roleId: String): Role? =
        Diskord.cachePolicy.guild.get(guildId)?.roles?.find { it.id == roleId }
            ?: fetchRoles(guildId)?.let { roles -> roles.first { it.id == roleId } }

}