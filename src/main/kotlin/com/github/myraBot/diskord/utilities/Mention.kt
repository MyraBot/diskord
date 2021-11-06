package com.github.myraBot.diskord.utilities

object Mention {
    fun user(id: String): String = "<@$id>"
    fun channel(id: String): String = "<#$id>"
    fun role(id: String): String = "<&@$id>"
}