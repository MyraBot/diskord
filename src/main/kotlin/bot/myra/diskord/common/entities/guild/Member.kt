package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.common.serializers.SInstant
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.common.utilities.Mention
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.behaviors.guild.MemberBehavior
import bot.myra.diskord.rest.bodies.BanInfo
import bot.myra.diskord.rest.request.RestClient
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
    val roles: List<String>,
    @Serializable(with = SInstant::class) @SerialName("joined_at") val joinedAt: Instant,
    @Serializable(with = SInstant::class) @SerialName("premium_since") val premiumSince: Instant? = null,
    val deaf: Boolean? = null,
    val mute: Boolean? = null,
    val pending: Boolean = false,
    val permissions: String? = null,
)

@Suppress("unused", "MemberVisibilityCanBePrivate")
@Serializable
data class Member(
    @JsonNames("guildId", "guild_id") @SerialName("guild_id") override val guildId: String,
    val user: User,
    val nick: String? = null,
    val avatar: String? = null,
    @SerialName("roles") val roleIds: List<String>,
    @Serializable(with = SInstant::class) @SerialName("joined_at") val joinedAt: Instant,
    @Serializable(with = SInstant::class) @SerialName("premium_since") val premiumSince: Instant? = null,
    val deaf: Boolean? = null,
    val mute: Boolean? = null,
    val pending: Boolean = false,
    val permissions: String? = null,
) : MemberBehavior {
    override val id: String = user.id
    val name: String get() = nick ?: user.username
    val mention: String = Mention.user(id)

    suspend fun getVoiceState(): VoiceState? = Diskord.cachePolicy.voiceState.view().find { it.userId == this.id && it.guildId == this.guildId }
    suspend fun getGuild(): Guild = Diskord.getGuild(guildId)!!
    suspend fun getRoles(): List<Role> = getGuild().roles.filter { roleIds.contains(it.id) }
    suspend fun getColour(): Color = getRoles().reversed().first { it.colour != Color.decode("0") }.colour
    suspend fun ban() = ban(null, null)
    suspend fun ban(deleteMessages: Int) = ban(deleteMessages, null)
    suspend fun ban(reason: String) = ban(null, reason)
    suspend fun ban(deleteMessages: Int?, reason: String?) = RestClient.execute(Endpoints.createGuildBan) {
        json = BanInfo(deleteMessages).toJson()
        logReason = reason
        arguments {
            arg("guild.id", guildId)
            arg("user.id", id)
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