package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.entities.Application
import com.github.myraBot.diskord.common.entities.Channel
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.message.Message
import io.ktor.http.*
import kotlinx.serialization.builtins.serializer


object Endpoints {
    const val baseUrl = "https://discord.com/api/v8"

    val createMessage = Route(HttpMethod.Post, "/channels/{channel.id}/messages", Message.serializer())
    val getChannel = Route(HttpMethod.Get, "/channels/{channel.id}", Channel.serializer())
    val getUser = Route(HttpMethod.Get, "/users/{user.id}", User.serializer())
    val getGuildMember = Route(HttpMethod.Get, "/guilds/{guild.id}/members/{user.id}", MemberData.serializer())
    val getBotApplication = Route(HttpMethod.Get, "/oauth2/applications/@me", Application.serializer())
    val acknowledgeInteraction = Route(HttpMethod.Post, "/interactions/{interaction.id}/{interaction.token}/callback", Unit.serializer())
    val getGuild = Route(HttpMethod.Get, "/guilds/{guild.id}", Guild.serializer())
}