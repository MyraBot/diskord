package diskord.common.entities.guild

import kotlinx.serialization.Serializable

@Serializable
data class UnavailableGuild(
        val id: String,
        val unavailable: Boolean
)
