package bot.myra.diskord.rest.request.error.rateLimits

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/topics/rate-limits#exceeding-a-rate-limit-rate-limit-response-structure)
 *
 * @property global A message saying you are being rate limited.
 * @property message The number of seconds to wait before submitting another request
 * @property retryAfter A value indicating if you are being globally rate limited or not
 */
@Serializable
data class RateLimit(
    val global: Boolean,
    val message: String,
    @SerialName("retry_after") val retryAfter: Float
)