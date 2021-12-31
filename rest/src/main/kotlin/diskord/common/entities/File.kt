package diskord.common.entities

import diskord.utilities.FileFormats

@Suppress("ArrayInDataClass")
data class File(
        val name: String,
        val type: FileFormats,
        val bytes: ByteArray
)
