package com.github.myraBot.diskord.utilities

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}

val CLIENT = HttpClient(CIO) {
    install(WebSockets)
}