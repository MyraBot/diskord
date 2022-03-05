package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.common.entities.Role
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.rest.CdnEndpoints
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.behaviors.guild.GuildBehavior
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/guild#guild-object-guild-structure)
 */
@Suppress("unused")
@Serializable
data class Guild(
    override val id: String,
    val name: String,
    @SerialName("icon") private val iconHash: String?,
    @SerialName("splash") private val splashHash: String?,
    @SerialName("discovery_splash") private val discoverySplashHash: String?,
    @SerialName("owner_id") internal val ownerId: String,
    val roles: List<Role>,
    val emojis: List<Emoji>,
    val features: List<String>,
    @SerialName("voice_states") val voiceStates: List<VoiceState> = emptyList(),
    @SerialName("vanity_url_code") val vanity: String?,
    val description: String?,
    @SerialName("premium_tier") val boostingTier: Int,
    @SerialName("preferred_locale") val locale: Locale,
    @SerialName("approximate_member_count") private val memberCount: Optional<Int> = Optional.Missing(),
    @SerialName("approximate_presence_count") private val onlineCount: Optional<Int> = Optional.Missing()
) : GuildBehavior {

    init {
        // Manually set the guild id of each voice state to the current guild id
        // as Discord doesn't provide the guild id in voice states of a GUILD_CREATE event
        voiceStates.forEach { it.guildId = id }
    }

    val icon: String? get() = iconHash?.let { CdnEndpoints.guildIcon.apply { arg("guild_id", id); arg("guild_icon", it) } }
    val splash: String? get() = splashHash?.let { CdnEndpoints.guildSplash.apply { arg("guild_id", id); arg("guild_splash", it) } }
    val discoverySplash: String? get() = discoverySplashHash?.let { CdnEndpoints.guildDiscoverySplash.apply { arg("guild_id", id); arg("guild_discovery_splash", it) } }

    suspend fun getOwnerAsync(): Deferred<Member?> = getMemberAsync(ownerId)

    fun getMemberCountAsync(): Deferred<Int> {
        val future = CompletableDeferred<Int>()
        RestClient.coroutineScope.launch {
            val guild = Diskord.getGuildAsync(id).await()
            val memberCount = guild!!.memberCount.value!!
            future.complete(memberCount)
        }
        return future
    }

    fun getOnlineCountAsync(): Deferred<Int> {
        val future = CompletableDeferred<Int>()
        RestClient.coroutineScope.launch {
            val guild = Diskord.getGuildAsync(id).await()
            val onlineCount = guild!!.onlineCount.value!!
            future.complete(onlineCount)
        }
        return future
    }

    fun getEmoji(name: String): Emoji? {
        return emojis.find { it.name == name }
    }

}

