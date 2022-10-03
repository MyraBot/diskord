package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.GatewayClosedReason
import bot.myra.diskord.gateway.OpCode
import bot.myra.diskord.gateway.OpPacket
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

internal class InvalidSessionHandler(
    override val gateway: Gateway
) : GatewayEventHandler(OpCode.INVALID_SESSION, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        val reconnect = packet.d?.jsonPrimitive?.boolean ?: false

        val reason = when (reconnect) {
            true  -> GatewayClosedReason.RECEIVED_INVALID_SESSION_RESUME
            false -> GatewayClosedReason.SESSION_TIMED_OUT
        }
        gateway.closeSocketConnection(reason)
        //gateway.connect(gateway.resumeUrl ?: gateway.url)
    }

}