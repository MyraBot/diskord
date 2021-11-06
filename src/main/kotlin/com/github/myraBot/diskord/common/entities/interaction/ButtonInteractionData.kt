package com.github.myraBot.diskord.common.entities.interaction

import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.interaction.components.items.button.Button
import com.github.myraBot.diskord.common.entities.message.Message

class ButtonInteractionData(
        internal val data: Interaction
) {
    val message: Message = data.message!!
    val member: MemberData? = data.member
    val button: Button get() = message.components.asSequence().flatMap { it.components }.first { it.id == data.interactionData?.customId }.let { return it.asButton() }
}