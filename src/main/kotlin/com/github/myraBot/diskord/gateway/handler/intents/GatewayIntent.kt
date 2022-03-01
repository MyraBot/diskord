package com.github.myraBot.diskord.gateway.handler.intents

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#gateway-intents)
 *
 * @constructor Used to store the index, which with I can calculate the intent number.
 *
 * @param index Discords used index to count the intents.
 */
@Suppress("unused")
enum class GatewayIntent(val index: Int) {
    GUILDS(0),
    GUILD_MEMBERS(1),
    GUILD_BANS(2),
    GUILD_EMOJIS_STICKERS(3),
    GUILD_INTEGRATIONS(4),
    GUILD_WEBHOOKS(5),
    GUILD_INVITES(6),
    GUILD_VOICE_STATES(7),
    GUILD_PRESENCES(8),
    GUILD_MESSAGES(9),
    GUILD_MESSAGE_REACTIONS(10),
    GUILD_MESSAGE_TYPING(11),
    DIRECT_MESSAGES(12),
    DIRECT_MESSAGE_REACTIONS(13),
    DIRECT_MESSAGE_TYPING(14);

    companion object {
        fun getID(intents: Set<GatewayIntent>): Int {
            return intents.map { 1 shl it.index }.reduce { acc, i -> acc or i }
        }
    }
}