package bot.myra.diskord.rest

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Application
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.entities.User
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommand
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.MemberData
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.utilities.ListSerializer
import io.ktor.http.HttpMethod
import kotlinx.serialization.builtins.serializer


object Endpoints {
    const val baseUrl = "https://discord.com/api/v8"

    val getGlobalApplicationCommands = Route(HttpMethod.Get, "/applications/{application.id}/commands", ListSerializer(SlashCommand.serializer()))
    val createMessage = Route(HttpMethod.Post, "/channels/{channel.id}/messages", Message.serializer())
    val getChannel = Route(HttpMethod.Get, "/channels/{channel.id}", ChannelData.serializer()) { channel, args ->
        Diskord.cachePolicy.channelCache.update(channel)
    }
    val getChannels = Route(HttpMethod.Get, "/guilds/{guild.id}/channels", ListSerializer(ChannelData.serializer())) { channels, args ->
        channels.forEach {
            Diskord.cachePolicy.channelCache.update(it)
            Diskord.cachePolicy.channelCache.guildAssociation.update(it)
        }
    }
    val getUser = Route(HttpMethod.Get, "/users/{user.id}", User.serializer()) { user, args ->
        Diskord.cachePolicy.userCache.update(user)
    }
    val getGuildMember = Route(HttpMethod.Get, "/guilds/{guild.id}/members/{user.id}", MemberData.serializer()) { data, args ->
        val member = Member.withUserInMember(data, args["guild.id"].toString())
        Diskord.cachePolicy.memberCache.update(member)
    }
    val listGuildMembers = Route(HttpMethod.Get, "/guilds/{guild.id}/members?limit={limit}&after=0", ListSerializer(MemberData.serializer())) { members, args ->
        members.forEach { data ->
            val member = Member.withUserInMember(data, args["guild.id"].toString())
            Diskord.cachePolicy.memberCache.update(member)
        }
    }
    val getBotApplication = Route(HttpMethod.Get, "/oauth2/applications/@me", Application.serializer())
    val acknowledgeInteraction = Route(HttpMethod.Post, "/interactions/{interaction.id}/{interaction.token}/callback", Unit.serializer())
    val getOriginalInteractionResponse = Route(HttpMethod.Get, "/webhooks/{application.id}/{interaction.token}/messages/@original", Message.serializer())
    val getGuild = Route(HttpMethod.Get, "/guilds/{guild.id}?with_counts=true", Guild.serializer()) { guild, args ->
        Diskord.cachePolicy.guildCache.update(guild)
    }
    val editMessage = Route(HttpMethod.Patch, "/channels/{channel.id}/messages/{message.id}", Message.serializer())
    val deleteMessage = Route(HttpMethod.Delete, "/channels/{channel.id}/messages/{message.id}", Unit.serializer())
    val getRoles = Route(HttpMethod.Get, "/guilds/{guild.id}/roles", ListSerializer(Role.serializer())) { roles, args ->
        roles.forEach {
            Diskord.cachePolicy.roleCache.update(it)
        }
    }
    val addReaction = Route(HttpMethod.Put, "/channels/{channel.id}/messages/{message.id}/reactions/{emoji}/@me", Unit.serializer())
    val addMemberRole = Route(HttpMethod.Put, "/guilds/{guild.id}/members/{user.id}/roles/{role.id}", Unit.serializer())
    val removeMemberRole = Route(HttpMethod.Delete, "/guilds/{guild.id}/members/{user.id}/roles/{role.id}", Unit.serializer())
    val createDm = Route(HttpMethod.Post, "/users/@me/channels", ChannelData.serializer())
    val createGuildBan = Route(HttpMethod.Put, "/guilds/{guild.id}/bans/{user.id}", Unit.serializer())
    val removeGuildBan = Route(HttpMethod.Delete, "/guilds/{guild.id}/bans/{user.id}", Unit.serializer())
    val getChannelMessage = Route(HttpMethod.Get, "/channels/{channel.id}/messages/{message.id}", Message.serializer())
}