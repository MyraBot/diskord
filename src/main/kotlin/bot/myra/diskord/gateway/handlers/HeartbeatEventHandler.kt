package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.OpCode
import bot.myra.diskord.gateway.OpPacket

internal class HeartbeatEventHandler(
    override val gateway: Gateway
) : GatewayEventHandler(OpCode.HEARTBEAT, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        gateway.sendHeartbeat()
        gateway.logger.trace("Sent heartbeat on request from discord")
    }

}