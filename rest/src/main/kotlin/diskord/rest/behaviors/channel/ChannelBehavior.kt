package diskord.rest.behaviors.channel

import diskord.rest.behaviors.Entity
import diskord.utilities.Mention

interface ChannelBehavior : Entity {
    val mention get() = Mention.channel(id)
}