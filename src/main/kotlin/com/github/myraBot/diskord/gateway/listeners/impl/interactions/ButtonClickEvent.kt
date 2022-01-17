package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.components.items.button.Button
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.behaviors.InteractionCreateBehavior
import kotlinx.coroutines.runBlocking

data class ButtonClickEvent(
        override val interaction: Interaction,
) : Event(), InteractionCreateBehavior {
    val message: Message = interaction.message.forceValue
    val guild: Guild? get() = runBlocking { GuildCache[interaction.guildId.value!!].await() }
    val member: Member? get() = interaction.member.value?.let { Member.withUserInMember(it, interaction.guildId.forceValue) }
    val button: Button
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == interaction.interactionComponentData?.customId }
            .let { return it.asButton() }
}