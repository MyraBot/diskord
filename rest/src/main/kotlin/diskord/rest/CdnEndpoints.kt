package diskord.rest

import diskord.utilities.FileFormats

/**
 * [Documentation](https://discord.com/developers/docs/reference#image-formatting-cdn-endpoints)
 */
object CdnEndpoints {
    val baseUrl = "https://cdn.discordapp.com/"

    val customEmoij = CdnRoute("emojis/{emoji_id}.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P, FileFormats.GIF))
    val guildIcon = CdnRoute("icons/guild_id/guild_icon.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P, FileFormats.GIF))
    val guildSplash = CdnRoute("splashes/guild_id/guild_splash.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P))
    val guildDiscoverySplash = CdnRoute("discovery-splashes/guild_id/guild_discovery_splash.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P))
    val guildBanner = CdnRoute("banners/guild_id/guild_banner.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P))
    val userBanner = CdnRoute("banners/user_id/user_banner.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P, FileFormats.GIF), true)
    val defaultUserAvatar = CdnRoute("embed/avatars/{discriminator.module5}}.png", listOf(FileFormats.PNG))
    val userAvatar = CdnRoute("avatars/{user_id}/{user_avatar}.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P, FileFormats.GIF), true)
    val guildMemberAvatar = CdnRoute("guilds/guild_id/users/user_id/avatars/member_avatar.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P, FileFormats.GIF), true)
    val applicationIcon = CdnRoute("app-icons/{application_id}/icon.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P))
    val applicationCover = CdnRoute("app-icons/{application_id}/{cover_image}.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P))
    val applicationAsset = CdnRoute("app-assets/{application_id}/{asset_id}.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P))
    val achievementIcon = CdnRoute("app-assets/application_id/achievements/achievement_id/icons/icon_hash.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P))
    val stickerPackBanner = CdnRoute("app-assets/710982414301790216/store/sticker_pack_banner_asset_id.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P))
    val teamIcon = CdnRoute("team-icons/team_id/team_icon.png ", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P))
    val roleIcon = CdnRoute("role-icons/role_id/role_icon.png", listOf(FileFormats.PNG, FileFormats.JPEG, FileFormats.WEB_P), true)
}

data class CdnRoute(val path: String, val formats: List<FileFormats>, val animatedPrefix: Boolean = false) {
    fun apply(argBuilder: RouteArguments.() -> Unit): String {
        val args = RouteArguments().apply(argBuilder).entries
        var route = CdnEndpoints.baseUrl + path
        args.forEach { route = route.replace("{${it.first}}", it.second.toString()) }
        return route
    }
}