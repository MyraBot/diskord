package bot.myra.diskord.voice.gateway.models

import kotlinx.serialization.Serializable

@Serializable
data class ConnectionReadyPayload(
    val ssrc: Int,
    val ip: String,
    val port: Int,
    val modes: List<String>
)