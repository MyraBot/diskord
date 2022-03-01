package com.github.myraBot.diskord.common.entities.guild

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.JSON
import com.github.myraBot.diskord.common.caching.VoiceCache
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.common.toJson
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.behaviors.guild.MemberBehavior
import com.github.myraBot.diskord.rest.bodies.BanInfo
import com.github.myraBot.diskord.rest.request.RestClient
import com.github.myraBot.diskord.common.utilities.InstantSerializer
import com.github.myraBot.diskord.common.utilities.Mention
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.awt.Color
import java.time.Instant

@Serializable
data class MemberData(
    val user: User? = null,
    val nick: String? = null,
    val avatar: String? = null,
    val roles: Array<String>,
    @SerialName("joined_at") @Serializable(with = InstantSerializer::class) val joinedAt: Instant,
    @SerialName("premium_since") @Serializable(with = InstantSerializer::class) val premiumSince: Instant? = null,
    val deaf: Boolean? = null,
    val mute: Boolean? = null,
    val pending: Boolean = false,
    val permissions: String? = null,
)

@Suppress("unused")
@Serializable
data class Member(
    @JsonNames("guildId", "guild_id") @SerialName("guild_id") override val guildId: String,
    val user: User,
    val nick: String? = null,
    val avatar: String? = null,
    @SerialName("roles") val roleIds: Array<String>,
    @SerialName("joined_at") @Serializable(with = InstantSerializer::class) val joinedAt: Instant,
    @SerialName("premium_since") @Serializable(with = InstantSerializer::class) val premiumSince: Instant? = null,
    val deaf: Boolean? = null,
    val mute: Boolean? = null,
    val pending: Boolean = false,
    val permissions: String? = null,
) : MemberBehavior {
    override val id: String = user.id
    val name: String get() = nick ?: user.username
    val mention: String = Mention.user(id)

    fun getGuildAsync(): Deferred<Guild?> = Diskord.getGuildAsync(guildId)

    fun getRolesAsync(): Deferred<List<Role>> {
        val future = CompletableDeferred<List<Role>>()
        RestClient.coroutineScope.launch {
            val guild = getGuildAsync().await()
            val roles = guild!!.roles.filter { roleIds.contains(it.id) }
            future.complete(roles)
        }
        return future
    }

    fun getColourAsync(): Deferred<Color> {
        val future = CompletableDeferred<Color>()
        RestClient.coroutineScope.launch {
            val roles = getRolesAsync().await()
            val colour = roles.reversed()
                .first { it.colour != Color.decode("0") }
                .colour
            future.complete(colour)
        }
        return future
    }

    val voiceState: VoiceState? get() = VoiceCache.collect().flatten().find { it.userId == id }


    fun ban() = ban(null, null)
    fun ban(deleteMessages: Int) = ban(deleteMessages, null)
    fun ban(reason: String) = ban(null, reason)
    fun ban(deleteMessages: Int?, reason: String?): Deferred<Unit> {
        return RestClient.executeAsync(Endpoints.createGuildBan) {
            json = BanInfo(deleteMessages).toJson()
            logReason = reason
            arguments {
                arg("guild.id", guildId)
                arg("user.id", id)
            }
        }
    }

    companion object {
        fun withUser(member: MemberData, guildId: String, user: User): Member {
            val jsonMember = JSON.encodeToJsonElement(member).jsonObject
            val jsonUser = JSON.encodeToJsonElement(user).jsonObject
            return JsonObject(jsonMember.toMutableMap()
                .apply {
                    this["user"] = jsonUser
                    this["guildId"] = JsonPrimitive(guildId)
                })
                .let { JSON.decodeFromJsonElement(it) }
        }

        fun withUserInMember(member: MemberData, guildId: String): Member {
            val jsonMember = JSON.encodeToJsonElement(member).jsonObject
            return JsonObject(jsonMember.toMutableMap()
                .apply {
                    this["guildId"] = JsonPrimitive(guildId)
                })
                .let { JSON.decodeFromJsonElement(it) }
        }
    }

}