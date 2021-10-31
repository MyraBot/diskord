package com.github.myraBot.diskord.common.entityData.interaction

import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#button-object)
 *
 * @property id
 * @property name
 */
@Serializable
data class EmojiData(
        val id: String? = null,
        val name: String? = null,
        val animated: Boolean = false
)