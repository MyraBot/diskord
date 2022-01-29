package com.github.myraBot.diskord.rest.request

import com.github.myraBot.diskord.common.Arguments
import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.rest.Route
import com.github.myraBot.diskord.rest.request.impl.ChainPromise
import com.github.myraBot.diskord.rest.request.impl.MapPromise
import com.github.myraBot.diskord.rest.request.impl.NonNullMapPromise
import com.github.myraBot.diskord.rest.request.impl.Promise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

private val scope = CoroutineScope(Dispatchers.IO)

interface Promise<T> : HttpRequestClient<T> {
    companion object {
        fun <T> of(route: Route<T>, json: String? = null, files: List<File> = emptyList(), arguments: Arguments.() -> Unit = {}): Promise<T> {
            val args = Arguments().apply(arguments)
            val request = HttpRequest(route, json, files, args)
            return Promise(httpRequest = request)
        }

        fun <T> of(value: T?): Promise<T> {
            return Promise(value = value)
        }
    }

    val value: T?
    val httpRequest: HttpRequest<T>?

    suspend fun await(): T?
    suspend fun awaitNonNull(): T

    fun async(coroutineScope: CoroutineScope = scope)
    fun async(coroutineScope: CoroutineScope = scope, callback: suspend (T?) -> Unit)
    fun asyncNonNull(coroutineScope: CoroutineScope = scope, callback: suspend (T) -> Unit)

    fun <O> map(transform: suspend (T?) -> O?): MapPromise<T, O> {
        return MapPromise(null, null, this, transform)
    }

    fun <O> mapNonNull(transform: suspend (T) -> O): NonNullMapPromise<T, O> {
        return NonNullMapPromise(null, null, this, transform)
    }

    fun <O> then(transform: suspend (T?) -> com.github.myraBot.diskord.rest.request.Promise<O>): ChainPromise<T, O> {
        return ChainPromise(null, null, this, transform)
    }

    fun <O> thenNonNull(transform: suspend (T?) -> com.github.myraBot.diskord.rest.request.Promise<O>): ChainPromise<T, O> {
        return ChainPromise(null, null, this, transform)
    }

}