package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.GatewayReconnectReason
import bot.myra.diskord.gateway.OpCode
import bot.myra.diskord.gateway.OpPacket
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

internal class InvalidSessionHandler(
    override val gateway: Gateway
) : GatewayEventHandler(OpCode.INVALID_SESSION, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        @Suppress("MoveVariableDeclarationIntoWhen")
        val reconnect = packet.d?.jsonPrimitive?.boolean ?: false
        val reason = when (reconnect) {
            true  -> GatewayReconnectReason.INVALID_SESSION_RESUME
            false -> GatewayReconnectReason.INVALID_SESSION
        }
        gateway.reconnect(reason)
    }

}