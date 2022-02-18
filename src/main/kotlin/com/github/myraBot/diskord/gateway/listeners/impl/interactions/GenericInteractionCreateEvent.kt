package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.behaviors.InteractionCreateBehavior
import kotlinx.serialization.Serializable

@Serializable
abstract class GenericInteractionCreateEvent(
    open val data: Interaction,
) : Event(), InteractionCreateBehavior {
    override val interaction: Interaction get() = data

    val user: User get() = data.member?.user ?: data.user.value!!
}