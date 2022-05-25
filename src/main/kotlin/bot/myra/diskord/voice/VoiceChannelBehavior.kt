package bot.myra.diskord.voice

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.common.utilities.toJsonObj
import bot.myra.diskord.gateway.commands.VoiceUpdate
import bot.myra.diskord.gateway.events.impl.VoiceServerUpdateEvent
import bot.myra.diskord.gateway.handler.OptCode
import bot.myra.diskord.gateway.handler.intents.GatewayIntent
import bot.myra.kommons.debug
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.decodeFromJsonElement

interface VoiceChannelBehavior {
    val data: ChannelData

    suspend fun requestConnection(mute: Boolean = false, deaf: Boolean = false): VoiceConnection {
        if (GatewayIntent.GUILD_VOICE_STATES !in Diskord.intents) throw Exception("You have to enable the GUILD_VOICE_STATES intent in order to join voice channels")

        val guildId = data.guildId.value ?: throw Exception("A bot can only join guild channels")
        val state = VoiceUpdate(guildId, data.id, mute, deaf)
        debug(this::class) { "Requesting connection for guild ${state.guildId}" }
        val opcode = OptCode(op = 4, d = state.toJsonObj(), s = null, t = null)
        Diskord.gateway.send(opcode)

        val voiceStateUpdateAwait = asDeferredAsync {
            Diskord.gateway.eventDispatcher
                .filter { it.t == "VOICE_STATE_UPDATE" }
                .mapNotNull { it.d }
                .map { JSON.decodeFromJsonElement<VoiceState>(it) }
                .first { it.guildId == state.guildId }
        }
        val voiceServerUpdateAwait = asDeferredAsync {
            Diskord.gateway.eventDispatcher
                .filter { it.t == "VOICE_SERVER_UPDATE" }
                .mapNotNull { it.d }
                .map { JSON.decodeFromJsonElement<VoiceServerUpdateEvent>(it) }
                .first { it.guildId == state.guildId }
        }
        val (stateEvent, serverEvent) = awaitAll(voiceStateUpdateAwait, voiceServerUpdateAwait)
        debug(this::class) { "Received all information ➜ opening voice gateway connection" }
        return VoiceConnection(
            endpoint = (serverEvent as VoiceServerUpdateEvent).endpoint,
            session = (stateEvent as VoiceState).sessionId,
            token = serverEvent.token,
            guildId = state.guildId
        )
    }

    private fun <T> asDeferredAsync(scope: CoroutineScope = CoroutineScope(Dispatchers.Default), runnable: suspend () -> T): Deferred<T> {
        val future = CompletableDeferred<T>()
        scope.launch { future.complete(runnable.invoke()) }
        return future
    }

}