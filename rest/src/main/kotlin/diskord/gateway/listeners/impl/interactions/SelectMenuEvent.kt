package diskord.gateway.listeners.impl.interactions

import diskord.common.caching.GuildCache
import diskord.common.entities.applicationCommands.Interaction
import diskord.common.entities.applicationCommands.components.items.button.SelectMenu
import diskord.common.entities.guild.Guild
import diskord.common.entities.guild.Member
import diskord.common.entities.message.Message
import diskord.gateway.listeners.Event
import diskord.rest.behaviors.InteractionCreateBehavior
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class SelectMenuEvent(
        override val interaction: Interaction
) : Event(), InteractionCreateBehavior {
    val message: Message = interaction.message!!
    val guild: Guild get() = GuildCache[interaction.guildId!!]!!
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