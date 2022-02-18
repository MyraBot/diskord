package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.Optional
import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.user.UserFlag
import com.github.myraBot.diskord.common.isMissing
import com.github.myraBot.diskord.common.toJson
import com.github.myraBot.diskord.rest.CdnEndpoints
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.bodies.DmCreation
import com.github.myraBot.diskord.rest.request.promises.Promise
import com.github.myraBot.diskord.utilities.Mention
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/user#user-object)
 */
@Serializable
class User(
    val id: String,
    val username: String,
    val discriminator: String,
    @SerialName("avatar") private val avatarHash: String?,
    @SerialName("bot") val isBot: Boolean = false,
    val system: Boolean = false,
    @SerialName("mfa_enabled") val mfaEnabled: Boolean = false,
    private @Serializable(with = UserFlag.Serializer::class) @SerialName("public_flags") val flags: Optional<List<UserFlag>> = Optional.Missing()
) {
    val avatar: String
        get() = avatarHash?.let {
            CdnEndpoints.userAvatar.apply { arg("user_id", id); arg("user_avatar", avatarHash) }
        } ?: CdnEndpoints.defaultUserAvatar.apply { arg("user_discriminator", discriminator.toInt() % 5) }

    val asTag: String get() = "$username#$discriminator"

    val mention: String get() = Mention.user(id)

    val link: String get() = "https://discord.com/users/$id"

    suspend fun openDms(): Promise<DmChannel> = Promise
        .of(Endpoints.createDm) { json = DmCreation(id).toJson() }
        .map { data -> data?.let { DmChannel(it) } }

    suspend fun getFlags(): Promise<List<UserFlag>> {
        return if (!flags.isMissing()) Promise.of(flags.forceValue)
        else Diskord.getUser(id).mapNonNull { it.flags.forceValue }
    }

}