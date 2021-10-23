package com.github.myraBot.diskord.common.entities.embed

import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#embed-object-embed-footer-structure)
 *
 * @property url Url of image.
 */
@Serializable
data class Image(
        var url: String,
)