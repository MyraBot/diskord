package bot.myra.diskord.gateway.events.impl.interactions.slashCommands

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.gateway.events.types.EventBroker

open class SlashCommandEventBroker(
    val interaction: Interaction,
    override val diskord: Diskord
) : EventBroker() {

    override suspend fun choose() = when (interaction.guildId.missing) {
        true  -> PrivateSlashCommandEvent(interaction, diskord)
        false -> GuildSlashCommandEvent(interaction, diskord)
    }

}