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
        override val data: Interaction,
) : InteractionCreateEvent(data) {
    val message: Message = data.message.forceValue
    val guild: Guild? get() = runBlocking { GuildCache[data.guildId.value!!].await() }
    val member: Member? get() = data.member
    val button: Button
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == data.interactionComponentData?.customId }
            .let { return it.asButton() }
}