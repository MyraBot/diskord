package bot.myra.diskord.rest

import bot.myra.diskord.common.Arguments
import io.ktor.http.*
import kotlinx.serialization.KSerializer

data class Route<R>(
    val httpMethod: HttpMethod,
    val path: String,
    val serializer: KSerializer<R>,
    val cache: ((R, bot.myra.diskord.common.Arguments) -> Unit)? = null,
)