package com.github.myraBot.diskord.rest.request.promises

import com.github.myraBot.diskord.rest.Route
import com.github.myraBot.diskord.rest.request.HttpRequest
import com.github.myraBot.diskord.rest.request.HttpRequestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val scope = CoroutineScope(Dispatchers.IO)

open class Promise<T>(
    open val value: T? = null,
    open val httpRequest: HttpRequest<T>? = null
) : HttpRequestClient<T> {

    companion object {
        suspend fun <T> of(route: Route<T>, builder: suspend HttpRequest<T>.() -> Unit = {}): Promise<T> {
            val request = HttpRequest(route).apply { builder.invoke(this) }
            return Promise(httpRequest = request)
        }

        fun <T> of(value: T?): Promise<T> {
            return Promise(value = value)
        }
    }


    open suspend fun await(): T? = (httpRequest?.let { execute(it) } ?: value)

    open suspend fun awaitNonNull(): T = await()!!


    open fun async(env: CoroutineScope = scope) {
        env.launch { await() }
    }

    open fun async(env: CoroutineScope = scope, callback: suspend (T?) -> Unit) {
        env.launch {
            callback.invoke(await())
        }
    }

    open fun asyncWithError(env: CoroutineScope = scope, callback: suspend (Throwable?) -> Unit) {
        env.launch {
            try {
                awaitNonNull()
                callback.invoke(null)
            } catch (error: Exception) {
                callback.invoke(error)
            }
        }
    }

    open fun asyncNonNull(env: CoroutineScope = scope, callback: suspend (T) -> Unit) {
        env.launch { callback.invoke(awaitNonNull()) }
    }


    fun <O> map(transform: suspend (T?) -> O?): MapPromise<T, O> {
        return MapPromise(this, transform)
    }

    fun <O> mapNonNull(transform: suspend (T) -> O): NonNullMapPromise<T, O> {
        return NonNullMapPromise(this, transform)
    }


    fun <O> then(transform: suspend (T?) -> Promise<O>): ChainPromise<T, O> {
        return ChainPromise(this, transform)
    }

    fun <O> thenNonNull(transform: suspend (T?) -> Promise<O>): ChainPromise<T, O> {
        return ChainPromise(this, transform)
    }

}