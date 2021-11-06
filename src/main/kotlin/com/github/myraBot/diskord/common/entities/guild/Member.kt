package com.github.myraBot.diskord.common.entities.guild

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.utilities.InstantSerializer
import com.github.myraBot.diskord.utilities.JSON
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import java.time.Instant

@Serializable
data class MemberData(
        val user: User? = null,
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

@Serializable
data class Member(
        val user: User,
        val nick: String? = null,
        val avatar: String? = null,
        val roles: Array<String>,
        @SerialName("joined_at") @Serializable(with = InstantSerializer::class) val joinedAt: Instant,
        @SerialName("premium_since") @Serializable(with = InstantSerializer::class) val premiumSince: Instant? = null,
        val deaf: Boolean,
        val mute: Boolean,
        val pending: Boolean = false,
        val permissions: String? = null
) {
    companion object {
        fun withUser(member: MemberData, user: User): Member {
            val jsonMember = JSON.encodeToJsonElement(member).jsonObject
            val jsonUser = JSON.encodeToJsonElement(user).jsonObject
            return JsonObject(jsonMember.toMutableMap()
                .apply { this["user"] = jsonUser })
                .let { JSON.decodeFromJsonElement(it) }
        }

        fun withUserInMember(member: MemberData): Member {
            val json = JSON.encodeToJsonElement(member).jsonObject
            return JSON.decodeFromJsonElement(json)
        }
    }

    val id: String = user.id
    val name: String get() = nick ?: user.username
}