package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.OpCode
import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.gateway.events.impl.ReadyEvent
import bot.myra.diskord.gateway.events.impl.ResumedEvent
import bot.myra.diskord.gateway.events.impl.guild.MemberJoinEvent
import bot.myra.diskord.gateway.events.impl.guild.MemberRemoveEvent
import bot.myra.diskord.gateway.events.impl.guild.MemberUpdateEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelUpdateEvent
import bot.myra.diskord.gateway.events.impl.guild.create.GuildCreateEventBroker
import bot.myra.diskord.gateway.events.impl.guild.delete.GuildDeleteEventBroker
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.roles.RoleUpdateEvent
import bot.myra.diskord.gateway.events.impl.guild.voice.VoiceStateUpdateEvent
import bot.myra.diskord.gateway.events.impl.interactions.InteractionCreateEventBroker
import bot.myra.diskord.gateway.events.impl.message.BulkMessageDeleteEvent
import bot.myra.diskord.gateway.events.impl.message.MessageDeleteEvent
import bot.myra.diskord.gateway.events.impl.message.MessageUpdateEvent
import bot.myra.diskord.gateway.events.impl.message.create.MessageCreateEventBroker
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
                "CHANNEL_CREATE"      -> ChannelCreateEvent(json.decode())
                "CHANNEL_DELETE"      -> ChannelDeleteEvent(json.decode())
                "CHANNEL_UPDATE"      -> ChannelUpdateEvent(json.decode())
                "GUILD_CREATE"        -> GuildCreateEventBroker(json.decode())
                "GUILD_DELETE"        -> GuildDeleteEventBroker(json.decode())
                "GUILD_MEMBER_ADD"    -> MemberJoinEvent(json.decode())
                "GUILD_MEMBER_REMOVE" -> MemberRemoveEvent(json.decode())
                "GUILD_MEMBER_UPDATE" -> MemberUpdateEvent(json.decode())
                "INTERACTION_CREATE"  -> InteractionCreateEventBroker(json.decode())
                "MESSAGE_CREATE"      -> MessageCreateEventBroker(json.decode())
                "MESSAGE_UPDATE"      -> MessageUpdateEvent(json.decode())
                "MESSAGE_DELETE"      -> MessageDeleteEvent(json.decode())
                "MESSAGE_DELETE_BULK" -> BulkMessageDeleteEvent(json.decode())
                "GUILD_ROLE_CREATE"   -> json.decode<RoleCreateEvent>()
                "GUILD_ROLE_UPDATE"   -> json.decode<RoleUpdateEvent>()
                "GUILD_ROLE_DELETE"   -> json.decode<RoleDeleteEvent>()
                "VOICE_STATE_UPDATE"  -> VoiceStateUpdateEvent(json.decode())
                "READY"               -> json.decode<ReadyEvent>()
                else                  -> null
            }?.handle()
        } catch (e: SerializationException) {
            gateway.logger.error("Failed to deserialize ${packet.t!!} (${e.message}) = ${JSON.encodeToString(json)}")
        }
    }

    private inline fun <reified T> JsonElement.decode() = JSON.decodeFromJsonElement<T>(this)

}