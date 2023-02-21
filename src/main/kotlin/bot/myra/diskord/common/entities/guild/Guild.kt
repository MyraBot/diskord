package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.rest.CdnEndpoints
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.behaviors.guild.GuildBehavior
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
class Guild(
    override val diskord: Diskord,
    val data: GuildData
) : GuildBehavior {
    override val id get() = data.id
    val name get() = data.name
    val iconHash get() = data.iconHash
    val splashHash get() = data.splashHash
    val discoverySplashHash get() = data.discoverySplashHash
    val ownerId get() = data.ownerId
    val roles get() = data.roles
    val emojis get() = data.emojis
    val features get() = data.features
    val vanity get() = data.vanity
    val description get() = data.description
    val boostingTier get() = data.boostingTier
    val locale get() = data.locale
    val approximateMemberCount get() = data.approximateMemberCount
    val approximateOnlineCount get() = data.approximateOnlineCount

    override val guildData: GuildData get() = data

    val icon: String? get() = iconHash?.let { CdnEndpoints.guildIcon.apply { arg("guild_id", id); arg("guild_icon", it) } }
    val splash: String? get() = splashHash?.let { CdnEndpoints.guildSplash.apply { arg("guild_id", id); arg("guild_splash", it) } }
    val discoverySplash: String? get() = discoverySplashHash?.let { CdnEndpoints.guildDiscoverySplash.apply { arg("guild_id", id); arg("guild_discovery_splash", it) } }
}

/**
 * [Documentation](https://discord.com/developers/docs/resources/guild#guild-object-guild-structure)
 */
@Suppress("unused")
@Serializable
data class GuildData(
    val id: String,
    val name: String,
    @SerialName("icon")
    val iconHash: String?,
    val splashHash: String?,
    val discoverySplashHash: String?,
    @SerialName("owner_id") val ownerId: String,
    var roles: List<Role>,
    val emojis: List<Emoji>,
    val features: List<String>,
    val vanity: String?,
    val description: String?,
    @SerialName("premium_tier") val boostingTier: Int,
    @SerialName("preferred_locale") val locale: Locale,
    val approximateMemberCount: Optional<Int> = Optional.Missing(),
    val approximateOnlineCount: Optional<Int> = Optional.Missing(),
)