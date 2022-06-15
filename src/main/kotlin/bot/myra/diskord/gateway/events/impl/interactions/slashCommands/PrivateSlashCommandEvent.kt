package bot.myra.diskord.gateway.events.impl.interactions.slashCommands

import bot.myra.diskord.common.entities.applicationCommands.Interaction

/**
 * Slash command event which runs when a slash command got invoked
 * in the dms or in a group channel.
 *
 * @property interaction
 */
open class PrivateSlashCommandEvent(
    override val interaction: Interaction
) : GenericSlashCommandEvent(interaction)