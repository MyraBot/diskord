package bot.myra.diskord.voice.gateway.handlers

import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.models.Operations

internal class VoiceReadyEventHandler(
    gateway: VoiceGateway
) : VoiceGatewayEventHandler(Operations.READY, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        gateway.eventDispatcher.emit(packet)
    }

}