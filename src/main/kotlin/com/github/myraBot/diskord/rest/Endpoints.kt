package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.entities.Message
import io.ktor.http.*


object Endpoints {
    const val baseUrl = "https://discord.com/api/v6"

    val createMessage = Route<Message>(HttpMethod.Post, "/channels/{channel.id}/messages", Message.serializer())
}

class RouteArguments {
    val entries = mutableListOf<Pair<String, Any>>()
    fun arg(key: String, value: String) = entries.add(Pair(key, value))
}