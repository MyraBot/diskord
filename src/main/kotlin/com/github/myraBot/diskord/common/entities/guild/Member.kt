package com.github.myraBot.diskord.common.entities.guild

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.utilities.InstantSerializer
import com.github.myraBot.diskord.utilities.JSON
import com.github.myraBot.diskord.utilities.Mention
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
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
        val guildId: String,
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
        fun withUser(member: MemberData, guild: SimpleGuild, user: User): Member {
            val jsonMember = JSON.encodeToJsonElement(member).jsonObject
            val jsonUser = JSON.encodeToJsonElement(user).jsonObject
            return JsonObject(jsonMember.toMutableMap()
                .apply {
                    this["user"] = jsonUser
                    this["guildId"] = JsonPrimitive(guild.id)
                })
                .let { JSON.decodeFromJsonElement(it) }
        }

        fun withUserInMember(member: MemberData, guild: SimpleGuild): Member {
            val jsonMember = JSON.encodeToJsonElement(member).jsonObject
            return JsonObject(jsonMember.toMutableMap()
                .apply {
                    this["guildId"] = JsonPrimitive(guild.id)
                })
                .let { JSON.decodeFromJsonElement(it) }
        }
    }

    val id: String = user.id
    val name: String get() = nick ?: user.username
    val asMention: String = Mention.user(id)
    val guild: SimpleGuild = SimpleGuild(this.guildId)
}