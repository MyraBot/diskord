package com.github.myraBot.diskord.common.entities.embed

import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#embed-object-embed-footer-structure)
 *
 * @property name The name of the author. Displayed as the text.
 * @property url Url as hyperlink.
 * @property icon Url of author icon.
 */
@Serializable
data class Author(
        var name: String,
        var url: String? = null,
        var icon: String? = null
)
