package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.GatewayReconnectReason
import bot.myra.diskord.gateway.OpCode
import bot.myra.diskord.gateway.OpPacket

internal class ReconnectEventHandler(
    override val gateway: Gateway
) : GatewayEventHandler(OpCode.RECONNECT, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        gateway.reconnect(GatewayReconnectReason.RECONNECT)
    }

}