package com.github.myraBot.diskord.rest.request.promises

abstract class PromiseOperator<I, O>(
    open val promise: Promise<I>
) : Promise<O>(null, null)