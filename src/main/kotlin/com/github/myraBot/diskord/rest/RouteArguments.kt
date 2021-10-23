package com.github.myraBot.diskord.rest

class RouteArguments {
    val entries = mutableListOf<Pair<String, Any>>()
    fun arg(key: String, value: String) = entries.add(Pair(key, value))
}