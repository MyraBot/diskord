package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.caching.DoubleKey
import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.caching.MemberCache
import com.github.myraBot.diskord.common.caching.RoleCache
import com.github.myraBot.diskord.common.entities.Application
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.utilities.ListSerializer
import io.ktor.http.*
import kotlinx.serialization.builtins.serializer


object Endpoints {
    const val baseUrl = "https://discord.com/api/v8"

    val createMessage = Route(HttpMethod.Post, "/channels/{channel.id}/messages", Message.serializer())
    val getChannel = Route(HttpMethod.Get, "/channels/{channel.id}", ChannelData.serializer())
    val getChannels = Route(HttpMethod.Get, "/guilds/{guild.id}/channels", ListSerializer(ChannelData.serializer()))
    val getUser = Route(HttpMethod.Get, "/users/{user.id}", User.serializer())
    val getGuildMember = Route(HttpMethod.Get, "/guilds/{guild.id}/members/{user.id}", MemberData.serializer()) { member, args ->
        val m: Member = Member.withUserInMember(member, args["guild.id"].toString())
        MemberCache.cache[DoubleKey(m.guildId, m.id)] = m
    }
    val listGuildMembers = Route(HttpMethod.Get, "/guilds/{guild.id}/members?limit={limit}&after=0", ListSerializer(MemberData.serializer())) { members, args ->
        members.forEach { member ->
            val m = Member.withUserInMember(member, args["guild.id"].toString())
            MemberCache.cache[DoubleKey(m.guildId, m.id)] = m
        }
    }
    val getBotApplication = Route(HttpMethod.Get, "/oauth2/applications/@me", Application.serializer())
    val acknowledgeInteraction = Route(HttpMethod.Post, "/interactions/{interaction.id}/{interaction.token}/callback", Unit.serializer())
    val getOriginalInteractionResponse = Route(HttpMethod.Get, "/webhooks/{application.id}/{interaction.token}/messages/@original", Message.serializer())
    val getGuild = Route(HttpMethod.Get, "/guilds/{guild.id}?with_counts=true", Guild.serializer()) { guild, args ->
        GuildCache.cache[args["guild.id"].toString()] = guild
    }
    val editMessage = Route(HttpMethod.Patch, "/channels/{channel.id}/messages/{message.id}", Message.serializer())
    val getRoles = Route(HttpMethod.Get, "/guilds/{guild.id}/roles", ListSerializer(Role.serializer())) { roles, args ->
        roles.forEach {
            RoleCache.cache[DoubleKey(args["guild.id"].toString(), it.id)] = it
        }
    }
    val addReaction = Route(HttpMethod.Put, "/channels/{channel.id}/messages/{message.id}/reactions/{emoji}/@me", Unit.serializer())
    val addMemberRole = Route(HttpMethod.Put, "/guilds/{guild.id}/members/{user.id}/roles/{role.id}", Unit.serializer())
    val removeMemberRole = Route(HttpMethod.Delete, "/guilds/{guild.id}/members/{user.id}/roles/{role.id}", Unit.serializer())
    val createDm = Route(HttpMethod.Post, "/users/@me/channels", ChannelData.serializer())
    val createGuildBan = Route(HttpMethod.Put, "/guilds/{guild.id}/bans/{user.id}", Unit.serializer())
    val removeGuildBan = Route(HttpMethod.Delete, "/guilds/{guild.id}/bans/{user.id}", Unit.serializer())
}