package bot.myra.diskord.voice.gateway.handlers

import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

internal class VoiceHeartbeatAcknowledgeEventHandler(
    gateway: VoiceGateway
) : VoiceGatewayEventHandler(Operations.HEARTBEAT_ACKNOWLEDGE, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        if (packet.d?.jsonPrimitive?.long != gateway.lastTimestamp) gateway.logger.warn("Received non matching heartbeat")
        else gateway.logger.debug("Acknowledged heartbeat!")
    }

}