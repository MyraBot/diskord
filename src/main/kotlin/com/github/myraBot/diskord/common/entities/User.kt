package com.github.m5rian.discord.objects.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/user#user-object)
 */
@Serializable
data class User(
        val id: String,
        val username: String,
        val discriminator: String,
        val avatar: String,
        val bot: Boolean = false,
        val system: Boolean = false,
        @SerialName("mfa_enabled") val mfaEnabled: Boolean = false,
        //val locale: String,
        //val verified: Boolean,
        //val flags: Int,
        //@SerialName("public_flags") val publicFlags: Int
)