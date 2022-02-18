package com.github.myraBot.diskord.common.entities.guild

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.Emoji
import com.github.myraBot.diskord.common.entities.Locale
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.rest.CdnEndpoints
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.Optional
import com.github.myraBot.diskord.rest.behaviors.guild.GuildBehavior
import com.github.myraBot.diskord.rest.request.promises.Promise
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/guild#guild-object-guild-structure)
 */
@Serializable
data class Guild(
    override val id: String,
    val name: String,
    private @SerialName("icon") val iconHash: String?,
    private @SerialName("splash") val splashHash: String?,
    private @SerialName("discovery_splash") val discoverySplashHash: String?,
    internal @SerialName("owner_id") val ownerId: String,
    val roles: List<Role>,
    val emojis: List<Emoji>,
    @SerialName("voice_states") val voiceStates: List<VoiceState> = emptyList(),
    @SerialName("preferred_locale") val locale: Locale,
    @SerialName("approximate_member_count") private val memberCount: Optional<Int> = Optional.Missing(),
    @SerialName("approximate_presence_count") private val onlineCount: Optional<Int> = Optional.Missing()
) : GuildBehavior {

    init {
        // Manually set the guild id of each voice state to the current guild id
        // as Discord doesn't provide the guild id in voice states of a GUILD_CREATE event
        voiceStates.forEach { it.guildId = id }
    }

    val icon: String? get() = iconHash?.let { CdnEndpoints.guildIcon.apply { arg("guild.id", id); arg("guild_icon", it) } }
    val splash: String? get() = splashHash?.let { CdnEndpoints.guildSplash.apply { arg("guild.id", id); arg("guild_splash", it) } }
    val discoverySplash: String? get() = discoverySplashHash?.let { CdnEndpoints.guildDiscoverySplash.apply { arg("guild.id", id); arg("guild_discovery_splash", it) } }

    suspend fun getOwner(): Promise<Member> = getMember(ownerId)

    suspend fun getMemberCount() = Diskord.getGuild(this.id).mapNonNull { it.memberCount.value!! }.awaitNonNull()

    suspend fun getOnlineCount() = Diskord.getGuild(this.id).mapNonNull { it.onlineCount.value!! }.awaitNonNull()

    fun getEmoji(name: String): Emoji? {
        return emojis.find { it.name == name }
    }

}


