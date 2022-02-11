package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.common.entities.channel.DmChannel
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
) {
    val avatar: String
        get() = avatarHash?.let {
            CdnEndpoints.userAvatar.apply { arg("user_id", id); arg("user_avatar", avatarHash) }
        } ?: CdnEndpoints.defaultUserAvatar.apply { arg("user_discriminator", discriminator.toInt() % 5) }
    val asTag: String get() = "$username#$discriminator"
    val mention: String get() = Mention.user(id)
    fun openDms(): Promise<DmChannel> = Promise
        .of(Endpoints.createDm, DmCreation(id).toJson())
        .map { data -> data?.let { DmChannel(it) } }
}