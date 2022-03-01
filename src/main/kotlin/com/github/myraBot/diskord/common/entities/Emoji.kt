package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.common.utilities.Mention
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
) {
    val mention: String get() = if (id == null) name!! else Mention.emoji(name!!, id, animated)
}