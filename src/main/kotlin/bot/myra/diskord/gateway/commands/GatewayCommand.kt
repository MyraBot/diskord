package bot.myra.diskord.gateway.commands

import bot.myra.diskord.gateway.OpCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class GatewayCommand(
    @Transient val operation: OpCode? = null
)