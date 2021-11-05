package com.github.myraBot.diskord.common.entities.interaction

import com.github.myraBot.diskord.common.entities.Member
import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.common.entityData.interaction.InteractionData
import com.github.myraBot.diskord.common.entityData.interaction.interactionTypes.InteractionComponentData

/**
 * Represents any Interaction. This is only used in [com.github.myraBot.diskord.gateway.listeners.impl.interactions.InteractionCreateEvent].
 *
 * @property data [InteractionData], which contains all data of the event.
 */
class Interaction(
        private val data: InteractionData
) {
    val member: Member?
        get() = if (data.guildId == null) null
        else if (data.member == null) null
        else Member(data.guildId, data.member)
    val message: Message? get() = if (data.message == null) null else Message(data.message)
    val interaction: InteractionComponentData? = data.interactionData
}