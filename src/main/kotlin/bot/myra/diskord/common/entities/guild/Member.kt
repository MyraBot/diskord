package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.common.entities.user.UserData
import bot.myra.diskord.common.serializers.SInstant
import bot.myra.diskord.common.utilities.Mention
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.behaviors.guild.MemberBehavior
import bot.myra.diskord.rest.bodies.BanInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.awt.Color
import java.time.Instant

@Suppress("unused")
data class Member(
    val data: MemberData,
    override val diskord: Diskord
) : MemberBehavior {

    companion object {
        fun fromPartialMember(partialMember: PartialMemberData, guildId: String, user: UserData, diskord: Diskord): Member {
            val member = partialMember.complete(guildId, user)
            return Member(member, diskord)
        }

        /**
         * Creates a new member from a partial member where the user field is not null.
         *
         * @param partialMember
         * @param guildId
         * @param diskord
         * @return
         */
        fun fromPartialMember(partialMember: PartialMemberData, guildId: String, diskord: Diskord): Member {
            val member = partialMember.complete(guildId)
            return Member(member, diskord)
        }
    }

    override val guildId: String get() = data.guildId
    val user get() = data.user
    val nick get() = data.nick
    val avatar get() = data.avatar
    val roleIds get() = data.roles
    val joinedAt get() = data.joinedAt
    val premiumSince get() = data.premiumSince
    val deaf get() = data.deaf
    val mute get() = data.mute
    val pending get() = data.pending

    override val id: String = user.id
    val name: String get() = nick ?: user.username
    val mention: String = Mention.user(id)

    suspend fun getVoiceState(): VoiceState? {
        val data = diskord.cachePolicy.voiceState.view().find {
            it.userId == this.id && it.guildId == this.guildId
        }
        return data?.let { VoiceState(it, diskord) }
    }

    suspend fun getGuild() = diskord.getGuild(guildId).value!!

    suspend fun getRoles(): List<Role> = getGuild().roles.filter { roleIds.contains(it.id) }

    suspend fun getColour(): Color = getRoles().reversed().first { it.colour != Color.decode("0") }.colour

    suspend fun ban(deleteMessages: Int? = null, reason: String? = null) = diskord.rest.execute(Endpoints.createGuildBan) {
        json = BanInfo(deleteMessages).toJson()
        logReason = reason
        arguments {
            arg("guild.id", guildId)
            arg("user.id", id)
        }
    }

}

@Serializable
data class MemberData(
    @SerialName("guild_id") val guildId: String,
    val roles: List<String>,
    var user: UserData,
    val nick: String? = null,
    val avatar: String? = null,
    @Serializable(with = SInstant::class) @SerialName("joined_at") val joinedAt: Instant,
    @Serializable(with = SInstant::class) @SerialName("premium_since") val premiumSince: Instant? = null,
    val deaf: Boolean? = null,
    val mute: Boolean? = null,
    val pending: Boolean = false,
)

@Serializable
data class PartialMemberData(
    var user: UserData? = null,
    val nick: String? = null,
    val avatar: String? = null,
    val roles: List<String>,
    @Serializable(with = SInstant::class) @SerialName("joined_at") val joinedAt: Instant,
    @Serializable(with = SInstant::class) @SerialName("premium_since") val premiumSince: Instant? = null,
    val deaf: Boolean? = null,
    val mute: Boolean? = null,
    val pending: Boolean = false,
) {
    fun complete(guildId: String): MemberData =
        MemberData(guildId, roles, user ?: throw MissingException(), nick, avatar, joinedAt, premiumSince, deaf, mute, pending)

    fun complete(guildId: String, user: UserData): MemberData =
        MemberData(guildId, roles, user, nick, avatar, joinedAt, premiumSince, deaf, mute, pending)

    private class MissingException : Exception(
        "Tried to complete a partial member which is missing the 'user' field. Use #complete(guildId, user) instead, to provide the user manually"
    )

}