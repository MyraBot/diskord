package com.github.myraBot.diskord.gateway.listeners

import kotlin.reflect.KFunction

abstract class EventListener {
    val functions = mutableListOf<KFunction<*>>()
}