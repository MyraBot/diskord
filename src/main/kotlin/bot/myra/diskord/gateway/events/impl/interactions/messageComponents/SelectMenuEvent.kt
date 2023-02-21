package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionComponentData
import bot.myra.diskord.common.entities.applicationCommands.InteractionData
import bot.myra.diskord.common.entities.applicationCommands.components.SelectMenu
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class SelectMenuEventData(
    val interaction: InteractionData,
    val component: InteractionComponentData
)

class SelectMenuEvent(
    val interaction: Interaction,
    override val component: InteractionComponentData,
    override val diskord: Diskord
) : GenericMessageComponentEvent(interaction.data, component, diskord) {
    val message: Message get() = Message(interaction.message.value!!, diskord)
    val member: Member? get() = interaction.member
    val selectMenu: SelectMenu
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == component.customId }
            .let { return it.asSelectMenu() }
    val id: String get() = interaction.id
    val values: List<String> get() = interaction.interactionData.value!!.jsonObject["values"]!!.jsonArray.map { it.jsonPrimitive.content }

    suspend fun getGuild() = interaction.guildId.value?.let { diskord.getGuild(it) }

}