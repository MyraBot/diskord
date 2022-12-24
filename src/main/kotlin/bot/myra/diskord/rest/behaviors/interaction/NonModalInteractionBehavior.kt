package bot.myra.diskord.rest.behaviors.interaction

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.channel.TextChannel
import bot.myra.diskord.rest.behaviors.getChannel

interface NonModalInteractionBehavior {
    val interaction: Interaction
    suspend fun getChannel() = Diskord.getChannel<TextChannel>(interaction.channelId.value!!)
}