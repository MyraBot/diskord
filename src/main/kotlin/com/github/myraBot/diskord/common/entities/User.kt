package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.user.UserFlag
import com.github.myraBot.diskord.common.toJson
import com.github.myraBot.diskord.rest.CdnEndpoints
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.Optional
import com.github.myraBot.diskord.rest.bodies.DmCreation
import com.github.myraBot.diskord.rest.request.RestClient
import com.github.myraBot.diskord.common.utilities.Mention
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
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

    fun getBannerAsync(): Deferred<String?> {
        if (!bannerHash.missing) {
            val hash = bannerHash.value ?: return CompletableDeferred(null)
            val banner = CdnEndpoints.userBanner.apply {
                arg("user_id", id)
                arg("user_banner", hash)
            }
            return CompletableDeferred(banner)
        } else {
            val future = CompletableDeferred<String?>()
            RestClient.coroutineScope.launch {
                val user = Diskord.getUserAsync(id).await()
                val hash = user!!.bannerHash.value ?: future.complete(null).also { return@launch }
                val banner = CdnEndpoints.userBanner.apply {
                    arg("user_id", id)
                    arg("user_banner", hash)
                }
                future.complete(banner)
            }
            return future
        }
    }

    fun getFlagsAsync(): Deferred<List<UserFlag>> {
        return if (!flags.missing) CompletableDeferred(flags.value!!)
        else {
            val future = CompletableDeferred<List<UserFlag>>()
            RestClient.coroutineScope.launch {
                val user = Diskord.getUserAsync(id).await()
                future.complete(user!!.flags.value!!)
            }
            return future
        }
    }

    fun openDmsAsync(): Deferred<DmChannel?> {
        val future = CompletableDeferred<DmChannel?>()
        RestClient.coroutineScope.launch {
            val channel: ChannelData = RestClient.executeNullableAsync(Endpoints.createDm) {
                json = DmCreation(id).toJson()
            }.await() ?: return@launch Unit.also { future.complete(null) }
            future.complete(DmChannel(channel))
        }
        return future
    }

}