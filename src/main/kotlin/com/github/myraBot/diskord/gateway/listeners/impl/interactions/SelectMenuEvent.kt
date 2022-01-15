package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.components.items.button.SelectMenu
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.behaviors.InteractionCreateBehavior
import com.github.myraBot.diskord.rest.request.Promise
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class SelectMenuEvent(
        override val interaction: Interaction,
) : Event(), InteractionCreateBehavior {
    val message: Message = interaction.message!!
    fun getGuild(): Promise<Guild> = Diskord.getGuild(interaction.guildId!!)
    val member: Member? get() = interaction.member?.let { Member.withUserInMember(it, interaction.guildId!!) }
    val selectMenu: SelectMenu
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == interaction.interactionComponentData?.customId }
            .let { return it.asSelectMenu() }
    val id: String get() = interaction.id
    val values: List<String> get() = interaction.interactionDataJson!!.jsonObject["values"]!!.jsonArray.map { it.jsonPrimitive.content }
}