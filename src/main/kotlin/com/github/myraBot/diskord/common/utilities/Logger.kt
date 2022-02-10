package com.github.myraBot.diskord.common.utilities

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

suspend fun kInfo(klass: KClass<*>, message: suspend (Unit) -> Any?) = LoggerFactory.getLogger(klass.java).info(message.invoke(Unit).toString())
fun info(klass: KClass<*>, message: (Unit) -> Any?) = LoggerFactory.getLogger(klass.java).info(message.invoke(Unit).toString())

suspend fun kError(klass: KClass<*>, message: suspend (Unit) -> Any?) = LoggerFactory.getLogger(klass.java).error(message.invoke(Unit).toString())
fun error(klass: KClass<*>, message: (Unit) -> Any?) = LoggerFactory.getLogger(klass.java).error(message.invoke(Unit).toString())

suspend fun kDebug(klass: KClass<*>, message: suspend (Unit) -> Any?) = LoggerFactory.getLogger(klass.java).debug(message.invoke(Unit).toString())
fun debug(klass: KClass<*>, message: (Unit) -> Any?) = LoggerFactory.getLogger(klass.java).debug(message.invoke(Unit).toString())

suspend fun kTrace(klass: KClass<*>, message: suspend (Unit) -> Any?) = LoggerFactory.getLogger(klass.java).trace(message.invoke(Unit).toString())
fun trace(klass: KClass<*>, message: (Unit) -> Any?) = LoggerFactory.getLogger(klass.java).trace(message.invoke(Unit).toString())