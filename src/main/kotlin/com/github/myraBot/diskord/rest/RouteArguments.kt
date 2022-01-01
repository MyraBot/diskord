package com.github.myraBot.diskord.rest

class RouteArguments {
    val entries = mutableListOf<Pair<String, String>>()
    fun arg(key: String, value: Any) = entries.add(Pair(key, value.toString()))
}