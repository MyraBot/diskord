package com.github.myraBot.diskord.rest

import com.github.m5rian.discord.objects.entities.User
import com.github.myraBot.diskord.common.entities.Application
import com.github.myraBot.diskord.common.entities.Member
import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.common.entities.channel.Channel
import io.ktor.http.*


object Endpoints {
    const val baseUrl = "https://discord.com/api/v6"
    const val imageBaseUrl = "https://cdn.discordapp.com/"

    val createMessage = Route<Message>(HttpMethod.Post, "/channels/{channel.id}/messages", Message.serializer())
    val getChannel = Route<Channel>(HttpMethod.Get, "/channels/{channel.id}")
    val getUser = Route<User>(HttpMethod.Get, "/users/{user.id}", User.serializer())
    val getGuildMember = Route<Member>(HttpMethod.Get, "/guilds/{guild.id}/members/{user.id}", Member.serializer())
    val getBotApplication = Route<Application>(HttpMethod.Get, "/oauth2/applications/@me", Application.serializer())
}