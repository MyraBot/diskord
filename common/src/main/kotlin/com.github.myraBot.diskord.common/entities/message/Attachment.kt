package com.github.myraBot.diskord.common.entities.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](ephemeral? *)
 *
 * @property id Attachment id.
 * @property filename The name of the attached file.
 * @property description A description of the file.
 * @property size File size in bytes.
 * @property url Source url of the file.
 * @property proxyUrl A proxied url of the file.
 * @property height The height of the image in pixels. This can be null if the attachment is not an image.
 * @property width The width of the image in pixels. This can be null if the attachment is not an image.
 */
@Serializable
data class Attachment(
        val id: String,
        val filename: String,
        val description: String? = null,
        val size: Int,
        val url: String,
        @SerialName("proxy_url") val proxyUrl: String,
        val height: Int? = null,
        val width: Int? = null
)