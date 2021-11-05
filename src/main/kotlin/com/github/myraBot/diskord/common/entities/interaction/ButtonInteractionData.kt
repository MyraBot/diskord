package com.github.myraBot.diskord.common.entities.interaction

import com.github.myraBot.diskord.common.entities.Member
import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.common.entityData.components.items.button.ButtonData
import com.github.myraBot.diskord.common.entityData.interaction.InteractionData

class ButtonInteractionData(
        internal val data: InteractionData
) {
    val message: Message = Message(data.message!!)
    val member: Member = Member(data.guildId!!, data.member!!)
    val button: ButtonData get() = message.components.asSequence().flatMap { it.components }.first { it.id == data.interactionData?.customId }.let { return it.asButton() }

}