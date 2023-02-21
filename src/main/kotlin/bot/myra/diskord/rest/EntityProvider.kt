package bot.myra.diskord.rest

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.MemberCacheKey
import bot.myra.diskord.common.cache.cache
import bot.myra.diskord.common.cache.cacheEach
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommand
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommandData
import bot.myra.diskord.common.entities.channel.*
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.entities.message.MessageData
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.rest.request.Result
import bot.myra.diskord.rest.request.transformEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Suppress("unused")
interface EntityProvider {
    val diskord: Diskord

    suspend fun getApplicationCommands(): List<SlashCommand> =
        diskord.rest.execute<List<SlashCommandData>>(Endpoints.getGlobalApplicationCommands) {
            arguments { arg("application.id", diskord.id) }
        }
            .transformValue { list -> list.map { SlashCommand(it, diskord) } }
            .getOrThrow()

    suspend fun getUserNonNull(id: String): User =
        diskord.cachePolicy.user.get(id).orTry {
            diskord.rest.execute(Endpoints.getUser) {
                arguments { arg("user.id", id) }
            }.cache(diskord.cachePolicy.user)
        }.transformValue { User(it, diskord) }.getOrThrow()

    suspend fun getUser(id: String): Result<User> =
        diskord.cachePolicy.user.get(id).orTry {
            diskord.rest.execute(Endpoints.getUser) {
                arguments { arg("user.id", id) }
            }.cache(diskord.cachePolicy.user)
        }.transformValue { User(it, diskord) }

    suspend fun getBotUser(): User = getUserNonNull(diskord.id)

    suspend fun fetchMessages(channelId: String, max: Int = 100, before: String? = null, after: String? = null): Result<List<Message>> {
        if (before != null && after != null) throw IllegalArgumentException("Only one, before or after can be set")
        return diskord.rest.execute(Endpoints.getChannelMessages) {
            arguments { arg("channel.id", channelId) }
            queryParameter.apply {
                add("limit" to max)
                before?.let { add("before" to before) }
                after?.let { add("after" to after) }
            }
        }.transformValue { list -> list.map { Message(it, diskord) } }
    }

    suspend fun getMessage(channelId: String, messageId: String): Result<Message> =
        diskord.rest.execute<MessageData>(Endpoints.getChannelMessage) {
            arguments {
                arg("channel.id", channelId)
                arg("message.id", messageId)
            }
        }.transformValue { Message(it, diskord) }

    suspend fun getGuild(id: String): Result<Guild> {
        return diskord.cachePolicy.guild.get(id)
            .transformValue { Guild(diskord, it) }
            .orTry { fetchGuild(id) }
    }

    suspend fun fetchGuild(id: String): Result<Guild> = diskord.rest.execute(Endpoints.getGuild) {
        arguments { arg("guild.id", id) }
    }
        .cache(diskord.cachePolicy.guild)
        .transformValue { Guild(diskord, it) }

    fun getGuilds(): Flow<Guild> = flow {
        val copiedIds = diskord.guildIds.toList()
        copiedIds.forEach { id ->
            println("Pending guilds: ${diskord.unavailableGuilds}")
            val guild = diskord.unavailableGuilds[id]?.await()?.also { println("getting from pending guilds") } ?: getGuild(id).value
            guild?.let { emit(it) }
        }
    }

    suspend fun getGuildChannels(id: String): List<GenericChannel> = diskord.cachePolicy.channel.viewByGuild(id)?.map { GenericChannel(it, diskord) } ?: fetchGuildChannels(id)

    suspend fun fetchGuildChannels(id: String): List<GenericChannel> =
        diskord.rest.execute(Endpoints.getChannels) {
            arguments { arg("guild.id", id) }
        }
            .cacheEach(diskord.cachePolicy.channel)
            .transformEach { GenericChannel(it, diskord) }
            .getOrThrow()

    suspend fun getChannel(id: String): Result<GenericChannel> =
        diskord.cachePolicy.channel.get(id).orTry {
            diskord.rest.execute(Endpoints.getChannel) {
                arguments { arg("channel.id", id) }
            }.cache(diskord.cachePolicy.channel)
        }.transformValue { GenericChannel(it, diskord) }

    suspend fun getMember(guildId: String, userId: String): Result<Member> {
        val key = MemberCacheKey(guildId, userId)
        return diskord.cachePolicy.member.get(key).orTry {
            diskord.rest.execute(Endpoints.getGuildMember) {
                arguments {
                    arg("guild.id", guildId)
                    arg("user.id", userId)
                }
            }
                .transformValue { it.complete(guildId) }
                .cache(diskord.cachePolicy.member)
        }.transformValue { Member(it, diskord) }
    }

    suspend fun fetchGuildMembers(guildId: String, limit: Int = 100) =
        diskord.rest.execute(Endpoints.listGuildMembers) {
            arguments {
                arg("guild.id", guildId)
                arg("limit", limit)
            }
        }
            .transformEach { it.complete(guildId) }
            .cacheEach(diskord.cachePolicy.member)
            .transformEach { Member(it, diskord) }

    suspend fun getRoles(guildId: String): List<Role> = diskord.cachePolicy.guild.get(guildId).value?.roles ?: fetchRoles(guildId)

    suspend fun fetchRoles(guildId: String): List<Role> = diskord.rest.execute(Endpoints.getGuild) {
        arguments { arg("guild.id", guildId) }
    }.cache(diskord.cachePolicy.guild).getOrThrow().roles

    suspend fun getRole(guildId: String, roleId: String): Role? = getRoles(guildId).find { it.id == roleId }
}

suspend inline fun <reified T> EntityProvider.getChannel(id: String): Result<T> = getChannel(id).transformValue {
    when (T::class) {
        ChannelData::class  -> it.data
        DmChannel::class    -> DmChannel(it.data, diskord)
        TextChannel::class  -> TextChannel(it.data, diskord)
        VoiceChannel::class -> VoiceChannel(it.data, diskord)
        else                -> throw IllegalStateException("Unknown channel to cast: ${T::class.simpleName}")
    } as T
}