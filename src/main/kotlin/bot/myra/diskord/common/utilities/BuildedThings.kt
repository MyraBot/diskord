package bot.myra.diskord.common.utilities

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*

val GATEWAY_CLIENT = HttpClient(CIO) {
    install(WebSockets)
}