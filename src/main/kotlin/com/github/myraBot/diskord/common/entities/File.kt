package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.utilities.FileFormats

@Suppress("ArrayInDataClass")
data class File(
        val name: String,
        val type: FileFormats,
        val bytes: ByteArray
)
