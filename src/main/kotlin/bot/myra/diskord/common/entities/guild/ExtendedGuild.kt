package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.common.entities.guild.voice.VoiceStateData
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.behaviors.guild.GuildBehavior
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
class ExtendedGuild(
    val data: ExtendedGuildData,
    override val diskord: Diskord
) : GuildBehavior {
    override val id get() = this.data.id
    val name get() = this.data.name
    val iconHash get() = this.data.iconHash
    val splashHash get() = this.data.splashHash
    val discoverySplashHash get() = this.data.discoverySplashHash
    val ownerId get() = this.data.ownerId
    val roles get() = this.data.roles
    val emojis get() = this.data.emojis
    val features get() = this.data.features
    val vanity get() = this.data.vanity
    val description get() = this.data.description
    val boostingTier get() = this.data.boostingTier
    val locale get() = this.data.locale
    val approximateMemberCount get() = this.data.approximateMemberCount
    val approximateOnlineCount get() = this.data.approximateOnlineCount

    val joinedAt get() = this.data.joinedAt
    val large get() = this.data.large
    val available get() = this.data.available
    val memberCount get() = this.data.memberCount
    val voiceStates get() = this.data.voiceStates.map { VoiceState(it, diskord) }

    override val guildData: GuildData = GuildData(
        id, name, iconHash, splashHash, discoverySplashHash, ownerId, roles, emojis, features, vanity, description, boostingTier, locale, approximateMemberCount,
        approximateOnlineCount
    )

    @Suppress("MemberVisibilityCanBePrivate")
    fun asGuild(): Guild = Guild(diskord, guildData)
}

@Suppress("unused")
@Serializable
data class ExtendedGuildData(
    val id: String,
    val name: String,
    @SerialName("icon") val iconHash: String?,
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
    @SerialName("voice_states") val voiceStates: List<VoiceStateData>
) {
    val available get() = unavailable == null || !unavailable

    init {
        // Manually set the guild id of each voice state to the current guild id
        // as Discord doesn't provide the guild id in voice states of a GUILD_CREATE event
        voiceStates.forEach { it.guildId = id }
    }
}