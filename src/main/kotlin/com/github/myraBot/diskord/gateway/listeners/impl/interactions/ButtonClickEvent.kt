package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.interaction.ButtonInteractionData
import com.github.myraBot.diskord.common.entities.interaction.components.items.button.Button
import com.github.myraBot.diskord.common.entities.interaction.Interaction
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.behaviors.InteractionCreateBehavior


data class ButtonClickEvent(
        val data: ButtonInteractionData,
        override val interaction: Interaction = data.data
) : Event(), InteractionCreateBehavior {
    val member: MemberData? = data.member
    val message: Message = data.message
    val button: Button = data.button
}