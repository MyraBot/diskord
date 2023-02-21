package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.common.Diskord
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
    override val gateway: Gateway,
    val diskord: Diskord
) : GatewayEventHandler(OpCode.DISPATCH, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        gateway.sequence = packet.s ?: gateway.sequence
        gateway.eventFlow.emit(packet)

        val json = packet.d ?: return
        try {
            when (packet.t!!) {
                "RESUMED"             -> ResumedEvent(diskord)
                "CHANNEL_CREATE"      -> ChannelCreateEvent.deserialize(json, JSON, diskord)
                "CHANNEL_DELETE"      -> ChannelDeleteEvent.deserialize(json, JSON, diskord)
                "CHANNEL_UPDATE"      -> ChannelUpdateEvent.deserialize(json, JSON, diskord)
                "GUILD_CREATE"        -> GuildCreateEventBroker.deserialize(json, JSON, diskord)
                "GUILD_DELETE"        -> GuildDeleteEventBroker.deserialize(json, JSON, diskord)
                "GUILD_MEMBER_ADD"    -> MemberJoinEvent.deserialize(json, JSON, diskord)
                "GUILD_MEMBER_REMOVE" -> MemberRemoveEvent.deserialize(json, JSON, diskord)
                "GUILD_MEMBER_UPDATE" -> MemberUpdateEvent.deserialize(json, JSON, diskord)
                "INTERACTION_CREATE"  -> InteractionCreateEventBroker.deserialize(json, JSON, diskord)
                "MESSAGE_CREATE"      -> MessageCreateEventBroker.deserialize(json, JSON, diskord)
                "MESSAGE_UPDATE"      -> MessageUpdateEvent.deserialize(json, JSON, diskord)
                "MESSAGE_DELETE"      -> MessageDeleteEvent.deserialize(json, JSON, diskord)
                "MESSAGE_DELETE_BULK" -> BulkMessageDeleteEvent.deserialize(json, JSON, diskord)
                "GUILD_ROLE_CREATE"   -> RoleCreateEvent.deserialize(json, JSON, diskord)
                "GUILD_ROLE_UPDATE"   -> RoleUpdateEvent.deserialize(json, JSON, diskord)
                "GUILD_ROLE_DELETE"   -> RoleDeleteEvent.deserialize(json, JSON, diskord)
                "VOICE_STATE_UPDATE"  -> VoiceStateUpdateEvent.deserialize(json, JSON, diskord)
                "READY"               -> ReadyEvent.deserialize(json, JSON, diskord)
                else                  -> null
            }?.handle()
        } catch (e: SerializationException) {
            gateway.logger.error("Failed to deserialize ${packet.t!!} (${e.message}) = ${JSON.encodeToString(json)}")
        }
    }

    private inline fun <reified T> JsonElement.decode() = JSON.decodeFromJsonElement<T>(this)

}