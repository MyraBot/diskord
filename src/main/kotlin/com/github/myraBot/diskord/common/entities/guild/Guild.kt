package com.github.myraBot.diskord.common.entities.guild

import com.github.myraBot.diskord.rest.behaviors.Entity
import kotlinx.serialization.Serializable

@Serializable
data class Guild(
        override val id: String
) : Entity
