package com.github.myraBot.diskord.common.utilities.logging

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

suspend fun info(klass: KClass<*>, message: suspend (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).info(message.invoke(Unit).toString())
}

suspend fun error(klass: KClass<*>, message: suspend (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).error(message.invoke(Unit).toString())
}

suspend fun debug(klass: KClass<*>, message: suspend (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).debug(message.invoke(Unit).toString())
}

suspend fun trace(klass: KClass<*>, message: suspend (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).trace(message.invoke(Unit).toString())
}