package diskord.gateway.listeners.impl

import diskord.gateway.listeners.Event
import kotlinx.serialization.Serializable

@Serializable
class UnknownEvent : Event() {
}