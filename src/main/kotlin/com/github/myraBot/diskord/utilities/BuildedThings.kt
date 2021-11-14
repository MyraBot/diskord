package com.github.myraBot.diskord.utilities

import com.github.myraBot.diskord.Diskord
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.network.sockets.*
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}

val GATEWAY_CLIENT = HttpClient(CIO) {
    install(WebSockets)
}

val REST_CLIENT = HttpClient(CIO) {
    install(HttpTimeout) {
        connectTimeoutMillis = 5000
        requestTimeoutMillis = 5000
    }
    expectSuccess = false // Disables throwing exceptions
    defaultRequest { header("Authorization", "Bot ${Diskord.token}") }
}