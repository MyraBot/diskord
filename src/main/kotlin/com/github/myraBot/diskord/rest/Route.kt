package com.github.myraBot.diskord.rest

import com.github.m5rian.discord.CLIENT
import com.github.m5rian.discord.DiscordBot
import com.github.m5rian.discord.JSON
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer

class Route<R>(private val httpMethod: HttpMethod, private val path: String, private val serializer: KSerializer<R>) {

    suspend fun execute(json: String? = null, argBuilder: RouteArguments.() -> Unit = {}): R {
        val res = executeHttpRequest(json, argBuilder).readText()
        return JSON.decodeFromString(serializer, res)
    }

    /**
     *  Executes the Http request.
     *
     * @param json Json data to send with the request.
     * @param argBuilder Variables to replace.
     * @return Return a [HttpResponse] of the executed request.
     */
    private suspend fun executeHttpRequest(json: String? = null, argBuilder: RouteArguments.() -> Unit = {}): HttpResponse {
        val args = RouteArguments().apply(argBuilder).entries
        var route = Endpoints.baseUrl + path
        args.forEach { route = route.replace("{${it.first}}", it.second.toString()) }

        return CLIENT.request(route) {
            method = httpMethod
            json?.let { contentType(ContentType.Application.Json) }
            header("Authorization", "Bot ${DiscordBot.token}")
            json?.let { body = it }
        }
    }

}