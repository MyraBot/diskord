package bot.myra.diskord.rest

import io.ktor.http.*
import kotlinx.serialization.KSerializer

data class Route<R>(
    val httpMethod: HttpMethod,
    val path: String,
    val serializer: KSerializer<R>
)