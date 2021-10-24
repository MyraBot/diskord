package com.github.myraBot.diskord.rest

import com.github.m5rian.discord.objects.entities.UserData
import com.github.myraBot.diskord.common.entityData.ApplicationData
import com.github.myraBot.diskord.common.entityData.MemberData
import com.github.myraBot.diskord.common.entityData.channel.ChannelData
import com.github.myraBot.diskord.common.entityData.message.MessageData
import io.ktor.http.*


object Endpoints {
    const val baseUrl = "https://discord.com/api/v6"

    val createMessage = Route<MessageData>(HttpMethod.Post, "/channels/{channel.id}/messages", MessageData.serializer())
    val getChannel = Route<ChannelData>(HttpMethod.Get, "/channels/{channel.id}", ChannelData.serializer())
    val getUser = Route<UserData>(HttpMethod.Get, "/users/{user.id}", UserData.serializer())
    val getGuildMember = Route<MemberData>(HttpMethod.Get, "/guilds/{guild.id}/members/{user.id}", MemberData.serializer())
    val getBotApplication = Route<ApplicationData>(HttpMethod.Get, "/oauth2/applications/@me", ApplicationData.serializer())
}