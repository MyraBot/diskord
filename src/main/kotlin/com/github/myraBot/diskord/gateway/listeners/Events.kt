package com.github.myraBot.diskord.gateway.listeners

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.JSON
import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.UnavailableGuild
import com.github.myraBot.diskord.common.utilities.error
import com.github.myraBot.diskord.common.utilities.kError
import com.github.myraBot.diskord.gateway.OptCode
import com.github.myraBot.diskord.gateway.listeners.impl.ReadyEvent
import com.github.myraBot.diskord.gateway.listeners.impl.UnknownEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.*
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelDeleteEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelUpdateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.voice.VoiceStateUpdateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.interactions.InteractionCreateEvent
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
                "INTERACTION_CREATE" -> InteractionCreateEvent(JSON.decodeFromJsonElement(data))
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
            kError(this::class) { "An error occurred on the following event: ${income.t}:" }
            kError(this::class) { "Incoming message = ${income.d}" }
            e.printStackTrace()
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
            .map { it.kotlin.objectInstance }
            .forEach {
                if (it == null) {
                    error(this::class) { "Found a listener which is not an object, skipping..." }
                    return@forEach
                } else it.loadListeners()
            }
    }

}