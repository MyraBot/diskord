package diskord.gateway.listeners.impl.interactions

import diskord.common.caching.GuildCache
import diskord.common.entities.applicationCommands.Interaction
import diskord.common.entities.applicationCommands.components.items.button.Button
import diskord.common.entities.guild.Guild
import diskord.common.entities.guild.Member
import diskord.common.entities.message.Message
import diskord.gateway.listeners.Event
import diskord.rest.behaviors.InteractionCreateBehavior

data class ButtonClickEvent(
        override val interaction: Interaction
) : Event(), InteractionCreateBehavior {
    val message: Message = interaction.message!!
    val guild: Guild get() = GuildCache[interaction.guildId!!]!!
    val member: Member? get() = interaction.member?.let { Member.withUserInMember(it, interaction.guildId!!) }
    val button: Button
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == interaction.interactionComponentData?.customId }
            .let { return it.asButton() }
}