package com.github.m5rian.discord

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun info(klass: KClass<*>, message: (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).info(message.invoke(Unit).toString())
}

fun debug(klass: KClass<*>, message: (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).debug(message.invoke(Unit).toString())
}

fun trace(klass: KClass<*>, message: (Unit) -> Any?) {
    LoggerFactory.getLogger(klass.java).trace(message.invoke(Unit).toString())
}