package com.github.myraBot.diskord.rest.request.impl

import com.github.myraBot.diskord.rest.request.HttpRequest
import com.github.myraBot.diskord.rest.request.Promise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ChainPromise<I, O>(
        override val value: O?,
        override val httpRequest: HttpRequest<O>?,
        override val promise: Promise<I>,
        private val transform: suspend (I?) -> Promise<O>,
) : PromiseOperator<I, O>(promise) {

    override suspend fun await(): O? {
        val input: I? = promise.httpRequest?.let { promise.execute(it) } ?: promise.value
        val newPromise: Promise<O> = input.run { transform.invoke(this) }
        return newPromise.await()
    }

    override suspend fun awaitNonNull(): O = await()!!

    override fun async(coroutineScope: CoroutineScope) {
        coroutineScope.launch { promise.await() }
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