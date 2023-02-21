package bot.myra.diskord.common.entities.user

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.channel.DmChannel
import bot.myra.diskord.common.utilities.Mention
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.rest.CdnEndpoints
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.behaviors.Entity
import bot.myra.diskord.rest.bodies.DmCreation
import bot.myra.diskord.rest.request.Result
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
class User(
    val data: UserData,
    override val diskord: Diskord
) : Entity {
    override val id get() = data.id
    val username get() = data.username
    val discriminator get() = data.discriminator
    val avatarHash get() = data.avatarHash
    val isBot get() = data.isBot
    val system get() = data.system
    val bannerHash get() = data.bannerHash
    val mfaEnabled get() = data.mfaEnabled
    val flags get() = data.flags

    val avatar: String
        get() = avatarHash?.let {
            CdnEndpoints.userAvatar.apply { arg("user_id", id); arg("user_avatar", it) }
        } ?: CdnEndpoints.defaultUserAvatar.apply { arg("user_discriminator", discriminator.toInt() % 5) }
    val asTag: String get() = "$username#$discriminator"
    val mention: String get() = Mention.user(id)
    val link: String get() = "https://discord.com/users/$id"

    suspend fun getBanner(): String? {
        if (!bannerHash.missing) {
            val hash = bannerHash.value ?: return null
            val banner = CdnEndpoints.userBanner.apply {
                arg("user_id", id)
                arg("user_banner", hash)
            }
            return banner
        } else {
            val user = diskord.getUser(id).value
            val hash = user!!.bannerHash.value ?: return null
            return CdnEndpoints.userBanner.apply {
                arg("user_id", id)
                arg("user_banner", hash)
            }
        }
    }

    suspend fun getFlags(): List<UserFlag> {
        return if (!flags.missing) flags.value!!
        else diskord.getUser(id).value!!.flags.value!!
    }

    suspend fun openDms(): Result<DmChannel> {
        return diskord.rest.execute(Endpoints.createDm) {
            ignoreBadRequest = true
            json = DmCreation(id).toJson()
        }.transformValue { DmChannel(it, diskord) }
    }
}

/**
 * [Documentation](https://discord.com/developers/docs/resources/user#user-object)
 */
@Serializable
class UserData(
    val id: String,
    val username: String,
    val discriminator: String,
    @SerialName("avatar") val avatarHash: String?,
    @SerialName("bot") val isBot: Boolean = false,
    val system: Boolean = false,
    @SerialName("banner") val bannerHash: Optional<String?> = Optional.Missing(),
    @SerialName("mfa_enabled") val mfaEnabled: Boolean = false,
    @Serializable(with = UserFlag.Serializer::class) @SerialName("public_flags") val flags: Optional<List<UserFlag>> = Optional.Missing()
)