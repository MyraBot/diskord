package com.github.myraBot.diskord.gateway.listeners

import com.github.m5rian.discord.OptCode
import com.github.myraBot.diskord.Diskord
import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.UnavailableGuild
import com.github.myraBot.diskord.gateway.Websocket
import com.github.myraBot.diskord.gateway.listeners.impl.ReadyEvent
import com.github.myraBot.diskord.gateway.listeners.impl.UnknownEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.*
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelDeleteEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelUpdateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.interactions.InteractionCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.message.MessageCreateEvent
import com.github.myraBot.diskord.utilities.JSON
import com.github.myraBot.diskord.utilities.logging.error
import com.github.myraBot.diskord.utilities.logging.info
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.reflections.Reflections
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.valueParameters

object Events {

    suspend fun resolve(income: OptCode) {
        try {
            val data: JsonElement = income.d!!
            when (income.t) {
                "READY" -> JSON.decodeFromJsonElement<ReadyEvent>(data).also {
                    Websocket.session = it.sessionId
                    GuildCache.ids = it.guilds.map(UnavailableGuild::id).toMutableList()
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
                else -> JSON.decodeFromJsonElement<UnknownEvent>(data)
            }.call()
        } catch (e: Exception) {
            error(this::class) { "An error occurred on the following event: ${income.t}:" }
            error(this::class) { "Incoming message = ${income.d}" }
            e.printStackTrace()
        }
    }

    fun register(packageName: String, listeners: MutableList<EventListener>) {
        info(this::class) { "Registering discord event listeners" }

        // Load custom registered listeners
        listeners.forEach { listener -> loadListener(listener) }.also {

            // Load listeners by reflection
            if (packageName.isNotBlank()) {
                Reflections(packageName).getSubTypesOf(EventListener::class.java)
                    .map { it.kotlin.objectInstance }
                    .forEach { listener ->
                        if (listener == null) throw IllegalStateException("Make sure all listeners are objects!")
                        loadListener(listener)
                    }
            }

        }

    }

    private fun loadListener(listener: EventListener) {
        listener::class.declaredFunctions
            .filter { it.hasAnnotation<ListenTo>() }
            .filter {
                // Get the first parameter of the function, if the function has no parameter add it still to the listeners,
                // to execute also listeners with no parameters.
                val klass = it.valueParameters.firstOrNull()?.type?.classifier ?: return@filter true
                Event::class.isSuperclassOf(klass as KClass<*>)
            }.let {
                listener.functions.addAll(it) // Load all functions in the listener
                Diskord.listeners.add(listener) // Add listener with functions to the registered listeners
            }
    }

}