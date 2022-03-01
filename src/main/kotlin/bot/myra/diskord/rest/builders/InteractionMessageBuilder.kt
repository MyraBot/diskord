package bot.myra.diskord.rest.builders

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class InteractionMessageBuilder(
    val interaction: Interaction
) : MessageBuilder()