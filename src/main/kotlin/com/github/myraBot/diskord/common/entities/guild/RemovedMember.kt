package com.github.myraBot.diskord.common.entities.guild

import com.github.myraBot.diskord.common.entities.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemovedMember(
    val user: User,
    @SerialName("guild_id") val guildId: String
)