package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.OpCode
import bot.myra.diskord.gateway.OpPacket

internal class HeartbeatAcknowledgeEventHandler(
    override val gateway: Gateway
) : GatewayEventHandler(OpCode.HEARTBEAT_ACKNOWLEDGE, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        gateway.logger.debug("Acknowledged heartbeat!")
    }

}