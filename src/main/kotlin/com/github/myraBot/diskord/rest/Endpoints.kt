package com.github.myraBot.diskord.rest

import com.github.m5rian.discord.objects.entities.UserData
import com.github.myraBot.diskord.common.entityData.ApplicationData
import com.github.myraBot.diskord.common.entityData.MemberData
import com.github.myraBot.diskord.common.entityData.channel.ChannelData
import com.github.myraBot.diskord.common.entityData.message.MessageData
import io.ktor.http.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonObject


object Endpoints {
    const val baseUrl = "https://discord.com/api/v8"

    val createMessage = Route(HttpMethod.Post, "/channels/{channel.id}/messages", MessageData.serializer())
    val getChannel = Route(HttpMethod.Get, "/channels/{channel.id}", ChannelData.serializer())
    val getUser = Route(HttpMethod.Get, "/users/{user.id}", UserData.serializer())
    val getGuildMember = Route(HttpMethod.Get, "/guilds/{guild.id}/members/{user.id}", MemberData.serializer())
    val getBotApplication = Route(HttpMethod.Get, "/oauth2/applications/@me", ApplicationData.serializer())
    val acknowledgeInteraction = Route(HttpMethod.Post, "/interactions/{interaction.id}/{interaction.token}/callback", Unit.serializer())
}