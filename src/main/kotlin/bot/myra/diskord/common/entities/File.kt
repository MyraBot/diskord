package bot.myra.diskord.common.entities

import bot.myra.diskord.common.utilities.FileFormats

@Suppress("ArrayInDataClass")
data class File(
    val name: String,
    val type: FileFormats,
    val bytes: ByteArray
)
