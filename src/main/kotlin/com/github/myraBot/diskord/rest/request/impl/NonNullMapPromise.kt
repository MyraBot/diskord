package com.github.myraBot.diskord.rest.request.impl

import com.github.myraBot.diskord.rest.request.HttpRequest
import com.github.myraBot.diskord.rest.request.Promise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NonNullMapPromise<I, O>(
        override val value: O?,
        override val httpRequest: HttpRequest<O>?,
        override val promise: Promise<I>,
        private val transform: suspend (I) -> O,
) : PromiseOperator<I, O>(promise) {

    override suspend fun await(): O? {
        return (promise.httpRequest?.let { promise.execute(it) } ?: promise.value)?.let { transform.invoke(it) }
    }

    override suspend fun awaitNonNull(): O = await()!!

    override fun async(env: CoroutineScope) {
        env.launch { promise.await() }
    }

    override fun async(env: CoroutineScope, callback: suspend (O?) -> Unit) {
        env.launch {
            promise.await()?.let { transform.invoke(it) }
        }
    }

    override fun asyncNonNull(env: CoroutineScope, callback: suspend (O) -> Unit) {
        env.launch {
            promise.await()!!.let { transform.invoke(it) }
        }
    }

}