package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.slashCommands.SlashCommand
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.behaviors.InteractionCreateBehavior
import com.github.myraBot.diskord.utilities.JSON
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class SlashCommandEvent(
        override val interaction: Interaction
) : Event(), InteractionCreateBehavior {
    val command: SlashCommand get() = JSON.decodeFromJsonElement(interaction.interactionDataJson!!)
    val member: Member get() = Member.withUserInMember(interaction.member!!, interaction.guildId!!)
}