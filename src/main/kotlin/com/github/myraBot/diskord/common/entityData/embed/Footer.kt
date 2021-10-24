package com.github.myraBot.diskord.common.entityData.embed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#embed-object-embed-footer-structure)
 *
 * @property text Footer text.
 * @property icon Url of footer icon.
 */
@Serializable
data class Footer(
        var text: String,
        @SerialName("icon_url") val icon: String?,
)