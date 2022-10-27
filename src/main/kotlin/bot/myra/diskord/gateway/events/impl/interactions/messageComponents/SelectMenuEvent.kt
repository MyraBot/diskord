package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionComponentData
import bot.myra.diskord.common.entities.applicationCommands.components.SelectMenu
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SelectMenuEvent(
    override val interaction: Interaction,
    override val component: InteractionComponentData
) : GenericMessageComponentEvent(interaction, component) {
    val message: Message = interaction.message.value!!
    val member: Member? get() = interaction.member
    val selectMenu: SelectMenu
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == component.customId }
            .let { return it.asSelectMenu() }
    val id: String get() = interaction.id
    val values: List<String> get() = interaction.data.value!!.jsonObject["values"]!!.jsonArray.map { it.jsonPrimitive.content }

    suspend fun getGuild() = interaction.guildId.value?.let { Diskord.getGuild(it) }

}