package bot.myra.diskord.gateway.events

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.gateway.events.impl.ReadyEvent
import bot.myra.diskord.gateway.events.impl.guild.*
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelUpdateEvent
import bot.myra.diskord.gateway.events.impl.guild.voice.VoiceStateUpdateEvent
import bot.myra.diskord.gateway.events.impl.interactions.InteractionCreateEvent
import bot.myra.diskord.gateway.events.impl.message.MessageCreateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.serialization.json.decodeFromJsonElement
import org.reflections.Reflections
import java.util.concurrent.ForkJoinPool

object Events {

    val coroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())

    fun startResolver() {
        coroutineScope.launch {
            Diskord.gateway.eventDispatcher.collect { income ->
                val json = income.d ?: return@collect
                when (income.t!!) {
                    "CHANNEL_CREATE"      -> ChannelCreateEvent(JSON.decodeFromJsonElement(json))
                    "CHANNEL_DELETE"      -> ChannelDeleteEvent(JSON.decodeFromJsonElement(json))
                    "CHANNEL_UPDATE"      -> ChannelUpdateEvent(JSON.decodeFromJsonElement(json))
                    "GUILD_CREATE"        -> GenericGuildCreateEvent(JSON.decodeFromJsonElement(json))
                    "GUILD_DELETE"        -> GuildLeaveEvent(JSON.decodeFromJsonElement(json))
                    "GUILD_MEMBER_ADD"    -> MemberJoinEvent(JSON.decodeFromJsonElement(json))
                    "GUILD_MEMBER_REMOVE" -> MemberRemoveEvent(JSON.decodeFromJsonElement(json))
                    "GUILD_MEMBER_UPDATE" -> MemberUpdateEvent(JSON.decodeFromJsonElement(json))
                    "INTERACTION_CREATE"  -> InteractionCreateEvent(JSON.decodeFromJsonElement(json))
                    "MESSAGE_CREATE"      -> MessageCreateEvent(JSON.decodeFromJsonElement(json))
                    "VOICE_STATE_UPDATE"  -> VoiceStateUpdateEvent(JSON.decodeFromJsonElement(json))
                    "READY"               -> JSON.decodeFromJsonElement<ReadyEvent>(json)
                    else                  -> null
                }?.call()
            }
        }
    }

    /**
     * Load listeners by reflection.
     * Finds and loads all listeners in a specific package.
     *
     * @param packageName The package name to search for listeners.
     */
    fun findListeners(packageName: String) {
        Reflections(packageName).getSubTypesOf(EventListener::class.java)
            .mapNotNull { it.kotlin.objectInstance }
            .forEach { it.loadListeners() }
    }

}