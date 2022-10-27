package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.rest.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
data class Guild(
    override val id: String,
    override val name: String,
    override val iconHash: String?,
    override val splashHash: String?,
    override val discoverySplashHash: String?,
    @SerialName("owner_id") override val ownerId: String,
    override var roles: List<Role>,
    override val emojis: List<Emoji>,
    override val features: List<String>,
    override val vanity: String?,
    override val description: String?,
    @SerialName("premium_tier") override val boostingTier: Int,
    @SerialName("preferred_locale") override val locale: Locale,
    override val approximateMemberCount: Optional<Int> = Optional.Missing(),
    override val approximateOnlineCount: Optional<Int> = Optional.Missing(),
) : GenericGuild()