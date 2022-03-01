package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.components.items.button.SelectMenu
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class SelectMenuEvent(
    override val data: Interaction,
) : GenericInteractionCreateEvent(data) {
    val message: Message = data.message.value!!
    val member: Member? get() = data.member
    val selectMenu: SelectMenu
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == data.interactionComponentData?.customId }
            .let { return it.asSelectMenu() }
    val id: String get() = data.id
    val values: List<String> get() = data.interactionDataJson.value!!.jsonObject["values"]!!.jsonArray.map { it.jsonPrimitive.content }

    fun getGuildAsync(): Deferred<Guild?> = data.guildId.value?.let { Diskord.getGuildAsync(it) } ?: CompletableDeferred(value = null)

}