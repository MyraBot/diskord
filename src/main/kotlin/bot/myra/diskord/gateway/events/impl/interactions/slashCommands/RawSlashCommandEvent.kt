package bot.myra.diskord.gateway.events.impl.interactions.slashCommands

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.gateway.events.Event
import kotlinx.serialization.Serializable

/**
 * Class triggered when the event gets invoked.
 *
 * @property interaction
 */
@Serializable
data class RawSlashCommandEvent(
    val interaction: Interaction
) : Event() {

    override suspend fun handle() {
        when (interaction.guildId.missing) {
            true  -> PrivateSlashCommandEvent(interaction)
            false -> GuildSlashCommandEvent(interaction)
        }.call()
    }

}
