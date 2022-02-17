package com.github.myraBot.diskord.gateway.listeners.impl.interactions.slashCommands

import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.guild.Member

open class GuildSlashCommandEvent(data: Interaction) : SlashCommandEvent(data) {
    override val member: Member get() = data.member!!
}