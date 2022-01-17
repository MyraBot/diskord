package com.github.myraBot.diskord.rest.request.impl

import com.github.myraBot.diskord.rest.request.HttpRequest
import com.github.myraBot.diskord.rest.request.Promise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Promise<T>(
        override val value: T? = null,
        override val httpRequest: HttpRequest<T>? = null,
) : Promise<T> {

    override suspend fun await(): T? = httpRequest?.let { execute(it) } ?: value
    override suspend fun awaitNonNull(): T = await()!!

    override fun async(env: CoroutineScope) {
        env.launch { await() }
    }

    override fun async(env: CoroutineScope, callback: suspend (T?) -> Unit) {
        env.launch { callback.invoke(await()) }
    }

    override fun asyncNonNull(env: CoroutineScope, callback: suspend (T) -> Unit) {
        env.launch { callback.invoke(awaitNonNull()) }
    }

}