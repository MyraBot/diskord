package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.rest.Optional
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/guild#guild-object-guild-structure)
 */
@Suppress("unused")
@Serializable
data class DetailedGuild(
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

    @SerialName("joined_at") val joinedAt: Instant,
    val large: Boolean,
    private val unavailable: Boolean?,
    @SerialName("member_count") val memberCount: Int,
    @SerialName("voice_states") val voiceStates: List<VoiceState>
) : Guild() {
    val available = unavailable == null || !unavailable

    init {
        // Manually set the guild id of each voice state to the current guild id
        // as Discord doesn't provide the guild id in voice states of a GUILD_CREATE event
        voiceStates.forEach { it.guildId = id }
    }

}