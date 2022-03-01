package com.github.myraBot.diskord.common.entities.message

enum class MessageFlag(val code: Int) {
    CROSSPOSTED(0),
    IS_CROSSPOST(1),
    SUPPRESS_EMBEDS(2),
    SOURCE_MESSAGE_DELETED(3),
    URGENT(4),
    HAS_THREAD(5),
    EPHEMERAL(6),
    LOADING(7)
}