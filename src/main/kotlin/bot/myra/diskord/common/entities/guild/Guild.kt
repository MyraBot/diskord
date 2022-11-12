package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.behaviors.guild.GuildBehavior
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/guild#guild-object-guild-structure)
 */
@Suppress("unused")
@Serializable
class Guild(
    override val id: String,
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
) : GuildBehavior {
    override val guild: Guild = this
}