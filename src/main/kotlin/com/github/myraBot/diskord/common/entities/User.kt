package com.github.myraBot.diskord.common.entities

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
        @SerialName("avatar") private val avatarHash: String,
        @SerialName("bot") val isBot: Boolean = false,
        val system: Boolean = false,
        @SerialName("mfa_enabled") val mfaEnabled: Boolean = false,
) {
    val avatar: String get() = CdnEndpoints.userAvatar.apply { arg("user_id", id); arg("user_avatar", avatarHash) }
    val asTag: String = "$username#$discriminator"
}