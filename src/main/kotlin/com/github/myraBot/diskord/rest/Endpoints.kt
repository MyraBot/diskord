package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.entities.Application
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.guild.Guild
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
    val getGuildMember = Route(HttpMethod.Get, "/guilds/{guild.id}/members/{user.id}", MemberData.serializer())
    val listGuildMembers = Route(HttpMethod.Get, "/guilds/{guild.id}/members?limit={limit}&after=0", ListSerializer(MemberData.serializer()))
    val getBotApplication = Route(HttpMethod.Get, "/oauth2/applications/@me", Application.serializer())
    val acknowledgeInteraction = Route(HttpMethod.Post, "/interactions/{interaction.id}/{interaction.token}/callback", Unit.serializer())
    val getOriginalInteractionResponse = Route(HttpMethod.Get, "/webhooks/{application.id}/{interaction.token}/messages/@original", Message.serializer())
    val getGuild = Route(HttpMethod.Get, "/guilds/{guild.id}", Guild.serializer())
    val editMessage = Route(HttpMethod.Patch, "/channels/{channel.id}/messages/{message.id}", Message.serializer())
    val getRoles = Route(HttpMethod.Get, "/guilds/{guild.id}/roles", ListSerializer(Role.serializer()))
    val addReaction = Route(HttpMethod.Put, "/channels/{channel.id}/messages/{message.id}/reactions/{emoji}/@me", Unit.serializer())
    val addMemberRole = Route(HttpMethod.Put, "/guilds/{guild.id}/members/{user.id}/roles/{role.id}", Unit.serializer())
    val removeMemberRole = Route(HttpMethod.Delete, "/guilds/{guild.id}/members/{user.id}/roles/{role.id}", Unit.serializer())
    val createDm = Route(HttpMethod.Post, "/users/@me/channels", ChannelData.serializer())
}