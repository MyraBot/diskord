package com.github.myraBot.diskord.common.entities.guild

import com.github.myraBot.diskord.common.entities.Emoji
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.rest.behaviors.GuildBehavior
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
        @SerialName("voice_states") val voiceStates: List<VoiceState> = emptyList()
) : GuildBehavior
