package com.github.myraBot.diskord.utilities.logging

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun info(klass: KClass<*>, message: (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).info(message.invoke(Unit).toString())
}

fun error(klass: KClass<*>, message: (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).error(message.invoke(Unit).toString())
}

fun debug(klass: KClass<*>, message: (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).debug(message.invoke(Unit).toString())
}

fun trace(klass: KClass<*>, message: (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).trace(message.invoke(Unit).toString())
}