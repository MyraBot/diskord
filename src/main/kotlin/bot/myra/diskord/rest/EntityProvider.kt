package bot.myra.diskord.rest

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.MemberCacheKey
import bot.myra.diskord.common.cache.cache
import bot.myra.diskord.common.cache.cacheEach
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommand
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.rest.request.RestClient
import bot.myra.diskord.rest.request.Result

object EntityProvider {

    suspend fun getApplicationCommands(): List<SlashCommand> =
        RestClient.execute(Endpoints.getGlobalApplicationCommands) {
            arguments { arg("application.id", Diskord.id) }
        }.getOrThrow()

    suspend fun getUserNonNull(id: String): User =
        Diskord.cachePolicy.user.get(id).orTry {
            RestClient.execute(Endpoints.getUser) {
                arguments { arg("user.id", id) }
            }.cache(Diskord.cachePolicy.user)
        }.getOrThrow()

    suspend fun getUser(id: String): Result<User> =
        Diskord.cachePolicy.user.get(id).orTry {
            RestClient.execute(Endpoints.getUser) {
                arguments { arg("user.id", id) }
            }.cache(Diskord.cachePolicy.user)
        }

    suspend fun fetchMessages(channelId: String, max: Int = 100, before: String? = null, after: String? = null): Result<List<Message>> {
        if (before != null && after != null) throw IllegalArgumentException("Only one, before or after can be set")
        return RestClient.execute(Endpoints.getChannelMessages) {
            arguments { arg("channel.id", channelId) }
            queryParameter.add("limit" to max)
            before?.let { queryParameter.add("before" to before) }
            after?.let { queryParameter.add("after" to after) }
        }
    }

    suspend fun getMessage(channelId: String, messageId: String) =
        RestClient.execute(Endpoints.getChannelMessage) {
            arguments {
                arg("channel.id", channelId)
                arg("message.id", messageId)
            }
        }

    suspend fun getGuild(id: String): Result<Guild> = Diskord.cachePolicy.guild.get(id).orTry { fetchGuild(id) }

    suspend fun fetchGuild(id: String): Result<Guild> = RestClient.execute(Endpoints.getGuild) {
        arguments { arg("guild.id", id) }
    }.cache(Diskord.cachePolicy.guild)

    suspend fun getGuildChannels(id: String): List<ChannelData> = Diskord.cachePolicy.channel.viewByGuild(id) ?: fetchGuildChannels(id)

    suspend fun fetchGuildChannels(id: String): List<ChannelData> =
        RestClient.execute(Endpoints.getChannels) {
            arguments { arg("guild.id", id) }
        }.cacheEach(Diskord.cachePolicy.channel).getOrThrow()

    suspend fun getChannel(id: String): Result<ChannelData> =
        Diskord.cachePolicy.channel.get(id).orTry {
            RestClient.execute(Endpoints.getChannel) {
                arguments { arg("channel.id", id) }
            }.cache(Diskord.cachePolicy.channel)
        }

    suspend fun getMember(guildId: String, userId: String): Result<Member> {
        val key = MemberCacheKey(guildId, userId)
        return Diskord.cachePolicy.member.get(key).orTry {
            RestClient.execute(Endpoints.getGuildMember) {
                arguments {
                    arg("guild.id", guildId)
                    arg("user.id", userId)
                }
            }.transformValue { Member.withUserInMember(it, guildId) }.cache(Diskord.cachePolicy.member)
        }
    }

    suspend fun fetchGuildMembers(guildId: String, limit: Int = 100) =
        RestClient.execute(Endpoints.listGuildMembers) {
            arguments {
                arg("guild.id", guildId)
                arg("limit", limit)
            }
        }.transformValue { list ->
            list.map { Member.withUserInMember(it, guildId) }
        }.cacheEach(Diskord.cachePolicy.member)

    suspend fun getRoles(guildId: String): List<Role> = Diskord.cachePolicy.guild.get(guildId).value?.roles ?: fetchRoles(guildId)

    suspend fun fetchRoles(guildId: String): List<Role> = RestClient.execute(Endpoints.getGuild) {
        arguments { arg("guild.id", guildId) }
    }.cache(Diskord.cachePolicy.guild).getOrThrow().roles

    suspend fun getRole(guildId: String, roleId: String): Role? = getRoles(guildId).find { it.id == roleId }

}