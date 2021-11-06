package com.github.myraBot.diskord.common.entities.guild

import com.github.myraBot.diskord.rest.behaviors.GuildBehavior
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/guild#guild-object-guild-structure)
 */
@Serializable
data class Guild(
        override val id: String,
        val name: String
) : GuildBehavior
