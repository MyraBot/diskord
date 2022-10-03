package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.OpCode
import bot.myra.diskord.gateway.OpPacket
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

internal sealed class GatewayEventHandler(val opcode: OpCode, open val gateway: Gateway) {
    abstract suspend fun onEvent(packet: OpPacket)
    fun listen() = gateway.coroutineScope.launch {
        gateway.incomingEvents.filter { it.op == opcode.code }.collect { onEvent(it) }
    }
}
