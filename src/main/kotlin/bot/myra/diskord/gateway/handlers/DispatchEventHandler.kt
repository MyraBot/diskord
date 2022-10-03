package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.OpCode
import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.gateway.events.impl.ReadyEvent
import bot.myra.diskord.gateway.events.impl.ResumedEvent
import bot.myra.diskord.gateway.events.impl.guild.*
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelUpdateEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleUpdateEvent
import bot.myra.diskord.gateway.events.impl.guild.voice.VoiceStateUpdateEvent
import bot.myra.diskord.gateway.events.impl.interactions.InteractionCreateEvent
import bot.myra.diskord.gateway.events.impl.message.BulkMessageDeleteEvent
import bot.myra.diskord.gateway.events.impl.message.MessageCreateEvent
import bot.myra.diskord.gateway.events.impl.message.MessageDeleteEvent
import bot.myra.diskord.gateway.events.impl.message.MessageUpdateEvent
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

internal class DispatchEventHandler(
    override val gateway: Gateway
) : GatewayEventHandler(OpCode.DISPATCH, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        gateway.sequence = packet.s ?: gateway.sequence
        gateway.eventFlow.emit(packet)

        val json = packet.d ?: return
        try {
            when (packet.t!!) {
                "RESUMED"             -> ResumedEvent()
                "CHANNEL_CREATE"      -> ChannelCreateEvent(JSON.decodeFromJsonElement(json))
                "CHANNEL_DELETE"      -> ChannelDeleteEvent(JSON.decodeFromJsonElement(json))
                "CHANNEL_UPDATE"      -> ChannelUpdateEvent(JSON.decodeFromJsonElement(json))
                "GUILD_CREATE"        -> GenericGuildCreateEvent(JSON.decodeFromJsonElement(json))
                "GUILD_DELETE"        -> GenericGuildDeleteEvent(JSON.decodeFromJsonElement(json))
                "GUILD_MEMBER_ADD"    -> MemberJoinEvent(JSON.decodeFromJsonElement(json))
                "GUILD_MEMBER_REMOVE" -> MemberRemoveEvent(JSON.decodeFromJsonElement(json))
                "GUILD_MEMBER_UPDATE" -> MemberUpdateEvent(JSON.decodeFromJsonElement(json))
                "INTERACTION_CREATE"  -> InteractionCreateEvent(JSON.decodeFromJsonElement(json))
                "MESSAGE_CREATE"      -> MessageCreateEvent(JSON.decodeFromJsonElement(json))
                "MESSAGE_UPDATE"      -> MessageUpdateEvent(JSON.decodeFromJsonElement(json))
                "MESSAGE_DELETE"      -> MessageDeleteEvent(JSON.decodeFromJsonElement(json))
                "MESSAGE_DELETE_BULK" -> BulkMessageDeleteEvent(JSON.decodeFromJsonElement(json))
                "GUILD_ROLE_CREATE"   -> json.decode<RoleCreateEvent>()
                "GUILD_ROLE_UPDATE"   -> json.decode<RoleUpdateEvent>()
                "GUILD_ROLE_DELETE"   -> json.decode<RoleDeleteEvent>()
                "VOICE_STATE_UPDATE"  -> VoiceStateUpdateEvent(JSON.decodeFromJsonElement(json))
                "READY"               -> JSON.decodeFromJsonElement<ReadyEvent>(json)
                else                  -> null
            }?.handle()
        } catch (e: SerializationException) {
            gateway.logger.error("Failed to deserialize ${packet.t!!} (${e.message}) = ${JSON.encodeToString(json)}")
        }
    }

    private inline fun <reified T> JsonElement.decode() = JSON.decodeFromJsonElement<T>(this)

}