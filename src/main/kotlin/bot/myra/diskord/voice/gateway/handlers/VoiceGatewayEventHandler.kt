package bot.myra.diskord.voice.gateway.handlers

import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

internal sealed class VoiceGatewayEventHandler(
    val opcode: Operations,
    open val gateway: VoiceGateway
) {

    abstract suspend fun onEvent(packet: OpPacket)

    fun listen() = gateway.coroutineScope.launch {
        gateway.incomingEvents.filter { it.op == opcode.code }.collect { onEvent(it) }
    }

}
