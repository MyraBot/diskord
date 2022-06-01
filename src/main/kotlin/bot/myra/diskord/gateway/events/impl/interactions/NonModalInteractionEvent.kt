package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.channel.TextChannel
import bot.myra.diskord.rest.behaviors.getChannel

abstract class NonModalInteractionEvent(interaction: Interaction) : GenericInteractionCreateEvent(interaction) {
    suspend fun getChannel(): TextChannel? = Diskord.getChannel(interaction.channelId.value!!)
}