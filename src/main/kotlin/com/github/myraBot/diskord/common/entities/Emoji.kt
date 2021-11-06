package com.github.myraBot.diskord.common.entities

import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#button-object)
 *
 * @property id
 * @property name
 */
@Serializable
data class Emoji(
        val id: String? = null,
        val name: String? = null,
        val animated: Boolean = false
)