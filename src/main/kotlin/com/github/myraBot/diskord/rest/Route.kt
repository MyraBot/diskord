package com.github.myraBot.diskord.rest

import com.github.m5rian.discord.trace
import com.github.myraBot.diskord.Diskord
import com.github.myraBot.diskord.utilities.CLIENT
import com.github.myraBot.diskord.utilities.JSON
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer

class Route<R>(private val httpMethod: HttpMethod, private val path: String, private val serializer: KSerializer<R>) {

    suspend fun execute(json: String? = null, argBuilder: RouteArguments.() -> Unit = {}): R? {
        val res = executeHttpRequest(json, argBuilder)
        val json = res.readText()
        trace(this::class) { res }

        if (res.status != HttpStatusCode.OK) return null
        if (serializer == Unit.serializer()) return Unit as R
        return JSON.decodeFromString(serializer, json)
    }

    suspend fun executeNonNull(json: String? = null, argBuilder: RouteArguments.() -> Unit = {}) : R  {
        val res = executeHttpRequest(json, argBuilder)
        val json = res.readText()
        trace(this::class) { res }

        if (res.status != HttpStatusCode.OK) {
            throw Exception(json)
        }
        if (serializer == Unit.serializer()) return Unit as R
        return JSON.decodeFromString(serializer, json)
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
            header("Authorization", "Bot ${Diskord.token}")
            json?.let { body = it }
        }
    }

}