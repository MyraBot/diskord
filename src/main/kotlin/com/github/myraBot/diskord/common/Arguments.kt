package com.github.myraBot.diskord.common

class Arguments {
    private val args = mutableMapOf<String, Any>()

    val entries get() = args.toMap()
    operator fun get(key: String) = args[key]

    fun arg(key: String, value: Any) {
        args[key] = value
    }
}