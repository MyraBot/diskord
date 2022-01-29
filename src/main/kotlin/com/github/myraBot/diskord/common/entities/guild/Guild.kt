package com.github.myraBot.diskord.common.entities.guild

import com.github.myraBot.diskord.common.entities.Emoji
import com.github.myraBot.diskord.common.entities.Locale
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.rest.behaviors.guild.GuildBehavior
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/guild#guild-object-guild-structure)
 */
@Serializable
data class Guild(
        override val id: String,
        val name: String,
        val icon: String?,
        val splash: String?,
        @SerialName("owner_id") val ownerId: String,
        val roles: List<Role>,
        val emojis: List<Emoji>,
        @SerialName("voice_states") val voiceStates: List<VoiceState> = emptyList(),
        @SerialName("preferred_locale") val locale: Locale
) : GuildBehavior {

    init {
        // Manually set the guild id of each voice state to the current guild id
        // as Discord doesn't provide the guild id in voice states of a GUILD_CREATE event
        voiceStates.forEach { it.guildId = id }
    }

}


