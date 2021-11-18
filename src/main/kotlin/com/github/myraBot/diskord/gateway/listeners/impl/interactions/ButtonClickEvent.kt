package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.components.items.button.Button
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.behaviors.InteractionCreateBehavior

data class ButtonClickEvent(
        override val interaction: Interaction
) : Event(), InteractionCreateBehavior {
    val message: Message = interaction.message!!
    val guild: Guild get() = GuildCache[interaction.guildId!!]!!
    val member: MemberData? get() = interaction.member
    val button: Button
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == interaction.interactionData?.customId }
            .let { return it.asButton() }
}