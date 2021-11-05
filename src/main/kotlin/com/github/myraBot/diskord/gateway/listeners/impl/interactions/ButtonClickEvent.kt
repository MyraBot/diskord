package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.entities.Member
import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.common.entities.interaction.ButtonInteractionData
import com.github.myraBot.diskord.common.entityData.components.items.button.ButtonData
import com.github.myraBot.diskord.common.entityData.interaction.InteractionData
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.behaviors.InteractionCreateBehavior


data class ButtonClickEvent(
        val data: ButtonInteractionData,
        override val interactionData: InteractionData = data.data
) : Event(), InteractionCreateBehavior {
    val member: Member = data.member
    val message: Message = data.message
    val button: ButtonData = data.button
}