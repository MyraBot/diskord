package com.github.myraBot.diskord.common.entities.guild

import com.github.myraBot.diskord.rest.behaviors.GuildBehavior
import kotlinx.serialization.Serializable

@Serializable
data class SimpleGuild(
        override val id: String
) : GuildBehavior