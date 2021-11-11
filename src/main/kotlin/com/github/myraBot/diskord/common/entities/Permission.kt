package com.github.myraBot.diskord.common.entities

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class Permission(val value: Long) {
    CREATE_INSTANT_INVITE(0x0000000001L),
    KICK_MEMBERS(0x0000000002L),
    BAN_MEMBERS(0x0000000004L),
    ADMINISTRATOR(0x0000000008L),
    MANAGE_CHANNELS(0x0000000010L),
    MANAGE_GUILD(0x0000000020L),
    ADD_REACTIONS(0x0000000040L),
    VIEW_AUDIT_LOG(0x0000000080L),
    PRIORITY_SPEAKER(0x0000000100L),
    STREAM(0x0000000200L),
    VIEW_CHANNEL(0x0000000400L),
    SEND_MESSAGES(0x0000000800L),
    SEND_TTS_MESSAGES(0x0000001000L),
    MANAGE_MESSAGES(0x0000002000L),
    EMBED_LINKS(0x0000004000L),
    ATTACH_FILES(0x0000008000L),
    READ_MESSAGE_HISTORY(0x0000010000L),
    MENTION_EVERYONE(0x0000020000L),
    USE_EXTERNAL_EMOJIS(0x0000040000L),
    VIEW_GUILD_INSIGHTS(0x0000080000L),
    CONNECT(0x0000100000L),
    SPEAK(0x0000200000L),
    MUTE_MEMBERS(0x0000400000L),
    DEAFEN_MEMBERS(0x0000800000L),
    MOVE_MEMBERS(0x0001000000L),
    USE_VAD(0x0002000000L),
    CHANGE_NICKNAME(0x0004000000L),
    MANAGE_NICKNAMES(0x0008000000L),
    MANAGE_ROLES(0x0010000000L),
    MANAGE_WEBHOOKS(0x0020000000L),
    MANAGE_EMOJIS_AND_STICKERS(0x0040000000L),
    USE_APPLICATION_COMMANDS(0x0080000000L),
    REQUEST_TO_SPEAK(0x0100000000L),
    MANAGE_THREADS(0x0400000000L),
    CREATE_PUBLIC_THREADS(0x0800000000L),
    CREATE_PRIVATE_THREADS(0x1000000000L),
    USE_EXTERNAL_STICKERS(0x2000000000L),
    SEND_MESSAGES_IN_THREADS(0x4000000000L),
    START_EMBEDDED_ACTIVITIES(0x8000000000L);

    internal object Serializer : KSerializer<List<Permission>> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Permission", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: List<Permission>) = encoder.encodeString(value.sum().toString())
        override fun deserialize(decoder: Decoder): List<Permission> = decoder.decodeString().toLong().let {
            values().filter { permission -> it and permission.value == permission.value }
        }
    }
}

fun List<Permission>.sum(): Long = this.fold(0L) { acc, permission -> acc or permission.value }

fun List<Permission>.has(vararg permissions: Permission): Boolean {
    val bitField = this.sum()
    permissions.forEach { if (bitField and it.value != it.value) return false }
    return true
}