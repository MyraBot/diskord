package bot.myra.diskord.common.utilities

import bot.myra.diskord.gateway.handler.OptCode
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.websocket.send
import org.slf4j.Logger

abstract class GenericGateway(
    val url: String,
    val logger: Logger
) {
    val client = HttpClient(CIO) {
        install(WebSockets)
        expectSuccess = true
    }

    lateinit var socket: DefaultClientWebSocketSession

    suspend fun send(opcode: OptCode) {
        logger.debug(">>> ${opcode.toJson()}")
        socket.send(opcode.toJson())
    }

}