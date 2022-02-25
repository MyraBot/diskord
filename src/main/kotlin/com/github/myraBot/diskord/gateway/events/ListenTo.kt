package com.github.myraBot.diskord.gateway.events

import kotlin.reflect.KClass

annotation class ListenTo(
        val event: KClass<out Event>
)
