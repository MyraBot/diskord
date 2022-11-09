package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.OpCode
import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.gateway.ReconnectReason

internal class ReconnectEventHandler(
    override val gateway: Gateway
) : GatewayEventHandler(OpCode.RECONNECT, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        gateway.logger.info("Received reconnect from Discord")
        gateway.reconnect(ReconnectReason.RequestReconnect())
    }

}