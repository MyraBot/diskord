package bot.myra.diskord.rest.behaviors.interaction

import bot.myra.diskord.common.entities.applicationCommands.InteractionData
import bot.myra.diskord.common.entities.channel.text.TextChannel
import bot.myra.diskord.rest.behaviors.DefaultBehavior
import bot.myra.diskord.rest.getChannel

interface NonModalInteractionBehavior : DefaultBehavior {
    val data: InteractionData

    suspend fun getChannel() = diskord.getChannel<TextChannel>(data.channel.value?.id!!)
}