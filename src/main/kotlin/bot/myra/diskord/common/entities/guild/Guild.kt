package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.rest.CdnEndpoints
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.behaviors.guild.GuildBehavior
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/guild#guild-object-guild-structure)
 */
@Suppress("unused")
@Serializable
abstract class Guild : GuildBehavior {
    abstract override val id: String
    abstract val name: String
    @SerialName("icon") abstract val iconHash: String?
    @SerialName("splash") abstract val splashHash: String?
    @SerialName("discovery_splash") abstract val discoverySplashHash: String?
    @SerialName("owner_id") abstract val ownerId: String
    abstract var roles: List<Role>
    abstract val emojis: List<Emoji>
    abstract val features: List<String>
    @SerialName("vanity_url_code") abstract val vanity: String?
    abstract val description: String?
    @SerialName("premium_tier") abstract val boostingTier: Int
    @SerialName("preferred_locale") abstract val locale: Locale
    @SerialName("approximate_member_count") open val approximateMemberCount: Optional<Int> = Optional.Missing()
    @SerialName("approximate_presence_count") open val approximateOnlineCount: Optional<Int> = Optional.Missing()

    val icon: String? get() = iconHash?.let { CdnEndpoints.guildIcon.apply { arg("guild_id", id); arg("guild_icon", it) } }
    val splash: String? get() = splashHash?.let { CdnEndpoints.guildSplash.apply { arg("guild_id", id); arg("guild_splash", it) } }
    val discoverySplash: String? get() = discoverySplashHash?.let { CdnEndpoints.guildDiscoverySplash.apply { arg("guild_id", id); arg("guild_discovery_splash", it) } }

    suspend fun getOwner(): Member? = getMember(ownerId)
    suspend fun getMemberCount(): Int = Diskord.fetchGuild(id)!!.approximateMemberCount.value!!
    suspend fun getOnlineCount(): Int = Diskord.fetchGuild(id)!!.approximateOnlineCount.value!!
    fun getEmoji(name: String): Emoji? = emojis.find { it.name == name }

}


