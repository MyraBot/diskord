package com.github.myraBot.diskord.rest

import com.github.m5rian.discord.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer

class Route<R>(private val httpMethod: HttpMethod, private val path: String, private val serializer: KSerializer<R>) {

    suspend fun execute (json: String, argBuilder: RouteArguments.() -> Unit = {}): R {
        val args = RouteArguments().apply(argBuilder).entries
        var route = Endpoints.baseUrl + path
        args.forEach { route = route.replace("{${it.first}}", it.second.toString()) }

        val response = CLIENT.request<HttpResponse>(route) {
            method = httpMethod
            contentType(ContentType.Application.Json)
            header("Authorization", "Bot ${DiscordBot.token}")
            body = json
        }.readText()

        return (JSON.decodeFromString(serializer, response))
    }

}