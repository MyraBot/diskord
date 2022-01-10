package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.caching.ChannelCache

interface GetTextChannelBehavior

inline fun <reified T> GetTextChannelBehavior.getChannel(id: String): T? = ChannelCache.getAs<T>(id)