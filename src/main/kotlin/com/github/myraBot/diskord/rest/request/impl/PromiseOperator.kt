package com.github.myraBot.diskord.rest.request.impl

import com.github.myraBot.diskord.rest.request.Promise

abstract class PromiseOperator<I, O>(
        open val promise: Promise<I>,
) : Promise<O>