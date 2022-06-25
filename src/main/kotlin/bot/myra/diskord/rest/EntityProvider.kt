package bot.myra.diskord.rest

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.DoubleKey
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommand
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.rest.request.RestClient

object EntityProvider {

    suspend fun getApplicationCommands(): List<SlashCommand> =
        RestClient.execute(Endpoints.getGlobalApplicationCommands) {
            arguments { arg("application.id", Diskord.id) }
        }

    suspend fun getUserNonNull(id: String): User =
        Diskord.cachePolicy.userCache.get(id)
            ?: RestClient.execute(Endpoints.getUser) {
                arguments { arg("user.id", id) }
            }.also { Diskord.cachePolicy.userCache.update(it) }

    suspend fun getUser(id: String): User? =
        Diskord.cachePolicy.userCache.get(id)
            ?: RestClient.executeNullable(Endpoints.getUser) {
                arguments { arg("user.id", id) }
            }?.also { Diskord.cachePolicy.userCache.update(it) }

    suspend fun fetchMessages(channelId: String, max: Int = 100, before: String? = null) = RestClient.execute(Endpoints.getChannelMessages) {
        arguments { arg("channel.id", channelId) }
        queryParameter.add("limit" to max)
        before?.let { queryParameter.add("before" to before) }
    }

    suspend fun getMessage(channelId: String, messageId: String) =
        RestClient.executeNullable(Endpoints.getChannelMessage) {
            arguments {
                arg("channel.id", channelId)
                arg("message.id", messageId)
            }
        }

    suspend fun getGuild(id: String): Guild? = Diskord.cachePolicy.guildCache.get(id) ?: fetchGuild(id)

    suspend fun fetchGuild(id: String): Guild? =
        RestClient.executeNullable(Endpoints.getGuild) {
            arguments { arg("guild.id", id) }
        }?.also { Diskord.cachePolicy.guildCache.update(it) }

    suspend fun getGuildChannels(id: String): List<ChannelData> = Diskord.cachePolicy.channelCache.guildAssociation.view(id) ?: fetchGuildChannels(id)

    suspend fun fetchGuildChannels(id: String): List<ChannelData> =
        RestClient.execute(Endpoints.getChannels) {
            arguments { arg("guild.id", id) }
        }.onEach { Diskord.cachePolicy.channelCache.updateChannel(it) }

    suspend fun getChannel(id: String): ChannelData? =
        Diskord.cachePolicy.channelCache.get(id)
            ?: RestClient.executeNullable(Endpoints.getChannel) {
                arguments { arg("channel.id", id) }
            }?.also { Diskord.cachePolicy.channelCache.update(it) }

    suspend fun getMember(guildId: String, userId: String): Member? {
        val key = DoubleKey(guildId, userId)
        return Diskord.cachePolicy.memberCache.get(key)
            ?: RestClient.executeNullable(Endpoints.getGuildMember) {
                arguments {
                    arg("guild.id", guildId)
                    arg("user.id", userId)
                }
            }?.let { Member.withUserInMember(it, guildId) }
                ?.also { Diskord.cachePolicy.memberCache.update(it) }
    }

    suspend fun fetchGuildMembers(guildId: String, limit: Int = 100) =
        RestClient.execute(Endpoints.listGuildMembers) {
            arguments {
                arg("guild.id", guildId)
                arg("limit", limit)
            }
        }.map { Member.withUserInMember(it, guildId) }

    suspend fun fetchRoles(guildId: String) = Diskord.cachePolicy.guildCache.get(guildId)?.roles ?: RestClient.execute(Endpoints.getRoles) {
        arguments { arg("guild.id", guildId) }
    }.also { Diskord.cachePolicy.guildCache.modify { guild -> guild.apply { roles = it } } }

    suspend fun getRole(guildId: String, roleId: String): Role? =
        Diskord.cachePolicy.guildCache.get(guildId)?.roles?.find { it.id == roleId }
            ?: RestClient.executeNullable(Endpoints.getRoles) {
                arguments { arg("guild.id", guildId) }
            }?.let { roles -> roles.first { it.id == roleId } }

}