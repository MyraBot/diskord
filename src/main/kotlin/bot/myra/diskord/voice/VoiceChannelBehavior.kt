package bot.myra.diskord.voice

import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.common.entities.guild.voice.VoiceStateData
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.common.utilities.toJsonObj
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.gateway.commands.VoiceUpdate
import bot.myra.diskord.gateway.events.impl.VoiceServerUpdateEvent
import bot.myra.diskord.rest.behaviors.DiskordObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.decodeFromJsonElement
import org.slf4j.LoggerFactory

interface VoiceChannelBehavior : DiskordObject {
    val data: ChannelData

    suspend fun requestConnection(mute: Boolean = false, deaf: Boolean = false): VoiceConnection {
        if (GatewayIntent.GUILD_VOICE_STATES !in diskord.intents) {
            throw Exception("You have to enable the GUILD_VOICE_STATES intent in order to join voice channels")
        }

        val guildId = data.guildId.value ?: throw Exception("A bot can only join guild channels")
        val state = VoiceUpdate(guildId, data.id, mute, deaf)

        val logger = LoggerFactory.getLogger("Voice")
        logger.debug("Requesting connection for guild ${state.guildId}")
        val packet = OpPacket(op = 4, d = state.toJsonObj(), s = null, t = null)
        diskord.gateway.send(packet)

        val scope = CoroutineScope(Dispatchers.Default + CoroutineName("VoiceConnection($guildId)"))
        val voiceStateUpdateAwait = scope.async {
            diskord.gateway.eventFlow
                .filter { it.t == "VOICE_STATE_UPDATE" }
                .mapNotNull { it.d }
                .map {
                    val data = JSON.decodeFromJsonElement<VoiceStateData>(it)
                    VoiceState(data, diskord)
                }
                .first { it.guildId == state.guildId }
        }
        val voiceServerUpdateAwait = scope.async {
            diskord.gateway.eventFlow
                .filter { it.t == "VOICE_SERVER_UPDATE" }
                .mapNotNull { it.d }
                .map { JSON.decodeFromJsonElement<VoiceServerUpdateEvent>(it) }
                .first { it.guildId == state.guildId }
        }

        val (stateEvent, serverEvent) = awaitAll(voiceStateUpdateAwait, voiceServerUpdateAwait)

        logger.debug("Received all information ➜ opening voice gateway connection")
        return VoiceConnection(
            endpoint = (serverEvent as VoiceServerUpdateEvent).endpoint,
            session = (stateEvent as VoiceState).sessionId,
            token = serverEvent.token,
            guildId = state.guildId,
            diskord = diskord,
            scope = scope
        )
    }

    suspend fun getMembers(): List<Member> = diskord.cachePolicy.voiceState.view()
        .filter { it.channelId === data.id }
        .mapNotNull {
            it.memberData?.let { member ->
                it.guildId?.let { guildId ->
                    member to guildId
                }
            }
        }
        .map { Member.fromPartialMember(it.first, it.second, diskord) }
}