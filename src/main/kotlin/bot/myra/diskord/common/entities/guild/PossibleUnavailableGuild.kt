package bot.myra.diskord.common.entities.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.rest.behaviors.Entity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Either an unavailable guild or a normal guild.
 *
 * @property id The guild id.
 * @property unavailable Whether the guild is unavailable.
 */
class PossibleUnavailableGuild(
    override val id: String,
    val unavailable: Boolean,
    private val json: JsonElement,
    private val decoder: Json,
    override val diskord: Diskord
) : Entity {
    val available get() = !unavailable

    companion object {
        @Serializable
        private data class Data(val id: String, val unavailable: Boolean? = null)

        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): PossibleUnavailableGuild {
            val data = decoder.decodeFromJsonElement<Data>(json)
            return PossibleUnavailableGuild(data.id, data.unavailable == true, json, decoder, diskord)
        }
    }

    fun asExtendedGuild(): ExtendedGuild? {
        if (unavailable) return null
        val data = decoder.decodeFromJsonElement<ExtendedGuildData>(json)
        return ExtendedGuild(data, diskord)
    }

}