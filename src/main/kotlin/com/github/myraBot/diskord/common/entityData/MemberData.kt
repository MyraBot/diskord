package com.github.myraBot.diskord.common.entityData

import com.github.m5rian.discord.objects.entities.UserData
import com.github.myraBot.diskord.utilities.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class MemberData(
        val user: UserData? = null,
        val nick: String? = null,
        val avatar: String? = null,
        val roles: Array<String>,
        @SerialName("joined_at") @Serializable(with = InstantSerializer::class) val joinedAt: Instant,
        @SerialName("premium_since") @Serializable(with = InstantSerializer::class) val premiumSince: Instant? = null,
        val deaf: Boolean,
        val mute: Boolean,
        val pending: Boolean = false,
        val permissions: String? = null
)