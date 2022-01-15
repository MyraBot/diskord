package com.github.myraBot.diskord.rest.request.impl

import com.github.myraBot.diskord.rest.request.HttpRequest
import com.github.myraBot.diskord.rest.request.Promise
import com.github.myraBot.diskord.rest.request.scope
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

    override fun async() {
        scope.launch { promise.await() }
    }

    override fun async(callback: suspend (O?) -> Unit) {
        scope.launch {
            promise.await()?.let { transform.invoke(it) }
        }
    }

    override fun asyncNonNull(callback: suspend (O) -> Unit) {
        scope.launch {
            promise.await()!!.let { transform.invoke(it) }
        }
    }

}