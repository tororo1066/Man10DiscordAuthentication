package tororo1066.man10discordauthentication

import org.bukkit.configuration.ConfigurationSection

object DiscordConfig {

    var token = ""
    var guildId = -1L
    var message = ""
    var verifiedRole = -1L

    fun load(config: ConfigurationSection) {
        token = config.getString("token") ?: ""
        guildId = config.getLong("guildId", -1)
        message = config.getString("message") ?: ""
        verifiedRole = config.getLong("verifiedRole", -1)
    }

    fun isValid(): Boolean {
        return token.isNotEmpty() && guildId != -1L && message.isNotEmpty() && verifiedRole != -1L
    }
}