package com.github.myraBot.diskord.rest.request.impl

import com.github.myraBot.diskord.rest.request.HttpRequest
import com.github.myraBot.diskord.rest.request.Promise
import com.github.myraBot.diskord.rest.request.scope
import kotlinx.coroutines.launch

class Promise<T>(
        override val value: T? = null,
        override val httpRequest: HttpRequest<T>? = null,
) : Promise<T> {

    override suspend fun await(): T? = httpRequest?.let { execute(it) } ?: value
    override suspend fun awaitNonNull(): T = await()!!

    override fun async() {
        scope.launch { await() }
    }

    override fun async(callback: suspend (T?) -> Unit) {
        scope.launch { callback.invoke(await()) }
    }

    override fun asyncNonNull(callback: suspend (T) -> Unit) {
        scope.launch { callback.invoke(awaitNonNull()) }
    }

}