package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.behaviors.guild.GuildBehavior
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
data class ExtendedGuild(
    override val id: String,
    val name: String,
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

    @SerialName("joined_at") val joinedAt: Instant,
    val large: Boolean,
    private val unavailable: Boolean?,
    @SerialName("member_count") val memberCount: Int,
    @SerialName("voice_states") val voiceStates: List<VoiceState>
) : GuildBehavior {
    override val guild: Guild = this.toGuild()
    val available = unavailable == null || !unavailable

    init {
        // Manually set the guild id of each voice state to the current guild id
        // as Discord doesn't provide the guild id in voice states of a GUILD_CREATE event
        voiceStates.forEach { it.guildId = id }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toGuild(): Guild = Guild(
        id, name, iconHash, splashHash, discoverySplashHash, ownerId, roles, emojis, features, vanity, description, boostingTier, locale, approximateMemberCount, approximateOnlineCount
    )

}