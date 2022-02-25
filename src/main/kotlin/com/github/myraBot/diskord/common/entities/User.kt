package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.user.UserFlag
import com.github.myraBot.diskord.common.toJson
import com.github.myraBot.diskord.rest.CdnEndpoints
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.Optional
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
    @SerialName("banner") private val bannerHash: Optional<String?> = Optional.Missing(),
    @SerialName("mfa_enabled") val mfaEnabled: Boolean = false,
    @Serializable(with = UserFlag.Serializer::class) @SerialName("public_flags") private val flags: Optional<List<UserFlag>> = Optional.Missing()
) {
    val avatar: String
        get() = avatarHash?.let {
            CdnEndpoints.userAvatar.apply { arg("user_id", id); arg("user_avatar", avatarHash) }
        } ?: CdnEndpoints.defaultUserAvatar.apply { arg("user_discriminator", discriminator.toInt() % 5) }
    val asTag: String get() = "$username#$discriminator"
    val mention: String get() = Mention.user(id)
    val link: String get() = "https://discord.com/users/$id"

    suspend fun getBanner(): Promise<String?> {
        if (!bannerHash.missing) {
            val hash = bannerHash.value ?: return Promise.of(null)
            val banner = CdnEndpoints.userBanner.apply {
                arg("user_id", id)
                arg("user_banner", hash)
            }
            return Promise.of(banner)
        } else {
            return Diskord.getUser(id).mapNonNull { user ->
                val hash = user.bannerHash.value ?: return@mapNonNull null
                val banner = CdnEndpoints.userBanner.apply {
                    arg("user_id", id)
                    arg("user_banner", hash)
                }
                return@mapNonNull banner
            }
        }
    }

    suspend fun getFlags(): Promise<List<UserFlag>> {
        return if (!flags.missing) Promise.of(flags.value!!)
        else Diskord.getUser(id).mapNonNull { it.flags.value!! }
    }

    suspend fun openDms(): Promise<DmChannel> = Promise
        .of(Endpoints.createDm) { json = DmCreation(id).toJson() }
        .map { data -> data?.let { DmChannel(it) } }

}