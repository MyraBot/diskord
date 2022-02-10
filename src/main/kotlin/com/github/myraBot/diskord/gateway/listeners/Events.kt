package com.github.myraBot.diskord.gateway.listeners

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.JSON
import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.UnavailableGuild
import com.github.myraBot.diskord.common.utilities.logging.error
import com.github.myraBot.diskord.common.utilities.logging.info
import com.github.myraBot.diskord.gateway.Cache
import com.github.myraBot.diskord.gateway.OptCode
import com.github.myraBot.diskord.gateway.listeners.impl.ReadyEvent
import com.github.myraBot.diskord.gateway.listeners.impl.UnknownEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.*
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelDeleteEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelUpdateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.voice.VoiceStateUpdateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.interactions.GenericInteractionCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.message.MessageCreateEvent
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.reflections.Reflections

object Events {

    suspend fun resolve(income: OptCode) {
        try {
            val data: JsonElement = income.d!!
            when (income.t) {
                "READY" -> JSON.decodeFromJsonElement<ReadyEvent>(data).also {
                    Diskord.websocket.session = it.sessionId
                    GuildCache.ids.addAll(it.guilds.map(UnavailableGuild::id))
                }
                "MESSAGE_CREATE" -> MessageCreateEvent(JSON.decodeFromJsonElement(data))
                "INTERACTION_CREATE" -> GenericInteractionCreateEvent(JSON.decodeFromJsonElement(data))
                "CHANNEL_CREATE" -> ChannelCreateEvent(JSON.decodeFromJsonElement(data))
                "CHANNEL_UPDATE" -> ChannelUpdateEvent(JSON.decodeFromJsonElement(data))
                "CHANNEL_DELETE" -> ChannelDeleteEvent(JSON.decodeFromJsonElement(data))
                "GUILD_MEMBER_ADD" -> MemberJoinEvent(JSON.decodeFromJsonElement(data))
                "GUILD_MEMBER_REMOVE" -> run {
                    val user: User = JSON.decodeFromJsonElement(data.jsonObject["user"]!!)
                    val guildId: String = data.jsonObject["guild_id"]!!.jsonPrimitive.content
                    MemberRemoveEvent(user, guildId)
                }
                "GUILD_MEMBER_UPDATE" -> MemberUpdateEvent(JSON.decodeFromJsonElement(data))
                "GUILD_DELETE" -> GuildDeleteEvent(JSON.decodeFromJsonElement(data))
                "GUILD_CREATE" -> GuildCreateEvent(JSON.decodeFromJsonElement(data))
                "VOICE_STATE_UPDATE" -> VoiceStateUpdateEvent(JSON.decodeFromJsonElement(data))
                else -> JSON.decodeFromJsonElement<UnknownEvent>(data)
            }.call()
        } catch (e: Exception) {
            error(this::class) { "An error occurred on the following event: ${income.t}:" }
            error(this::class) { "Incoming message = ${income.d}" }
            e.printStackTrace()
        }
    }

    /**
     * Loads all event listeners.
     *
     * @param listeners A list to register listeners manuel.
     * @param packageName A package name to search for listeners.
     */
    suspend fun register(listeners: MutableList<EventListener>, packageName: String?) {
        info(this::class) { "Registering discord event listeners" }

        Diskord.cache.map(Cache::cache).forEach(EventListener::loadAsCache) // Load listeners required for the cache
        listeners.forEach(EventListener::loadAsListener)
        //listeners.forEach { it.loadAsListener() } // Load custom registered listeners
        packageName?.let { findListeners(it) } // Load listeners by reflection
    }

    /**
     * Finds and loads all listeners in a specific package.
     *
     * @param packageName The package name to search for listeners.
     */
    private fun findListeners(packageName: String) {
        Reflections(packageName).getSubTypesOf(EventListener::class.java)
            .map { it.kotlin.objectInstance }
            .forEach { listener ->
                if (listener == null) throw IllegalStateException("Make sure all listeners are objects!")
                listener.loadAsListener()
            }
    }

}