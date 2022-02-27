package com.github.myraBot.diskord.common.utilities

import com.github.myraBot.diskord.common.Diskord
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

val GATEWAY_CLIENT = HttpClient(CIO) {
    install(WebSockets)
}