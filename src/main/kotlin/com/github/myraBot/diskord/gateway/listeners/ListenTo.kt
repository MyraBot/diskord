package com.github.myraBot.diskord.gateway.listeners

import kotlin.reflect.KClass

annotation class ListenTo(
        val event: KClass<out Event>
)
