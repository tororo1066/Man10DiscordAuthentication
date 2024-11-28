package tororo1066.man10discordauthentication

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.internal.utils.JDALogger
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.lang.SLang
import tororo1066.tororopluginapi.lang.SLang.Companion.sendTranslateMsg
import java.time.Duration
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import java.util.logging.Logger

class Man10DiscordAuthentication: SJavaPlugin(UseOption.SConfig), Man10DiscordAuthenticationAPI {

    companion object {
        var jda: JDA? = null
        lateinit var sLang: SLang

        var prefix = "§c[§bMan10DiscordAuthentication§c]§r"
        val authMap = HashMap<Int, UUID>()
        val processing = ArrayList<Long>()

        var reloading = false

        fun reload(sender: CommandSender): CompletableFuture<Boolean> {
            reloading = true
            authMap.clear()
            processing.clear()
            return CompletableFuture.supplyAsync {
                if (jda != null) {
                    val jda = jda!!
                    jda.shutdown()
                    if (!jda.awaitShutdown(Duration.ofSeconds(10))) {
                        jda.shutdownNow()
                    }
                }
                plugin.reloadConfig()
                prefix = plugin.config.getString("prefix") ?: prefix
                sLang = SLang(plugin, prefix)
                val jdaConfig = sConfig.getOrCreateConfig("config/discord")
                DiscordConfig.load(jdaConfig)
                if (DiscordConfig.isValid()) {
                    val jda = JDABuilder.createDefault(DiscordConfig.token).build()
                    jda.awaitReady()
                    jda.addEventListener(DiscordEventListener())
                    jda.getGuildById(DiscordConfig.guildId)?.updateCommands()?.addCommands(
                        Commands.slash("init", "初期化")
                    )?.queue() ?: run {
                        sender.sendTranslateMsg("jda.invalid_guild")
                        jda.shutdownNow()
                        return@supplyAsync false
                    }
                    this.jda = jda
                } else {
                    this.jda = null
                    sender.sendTranslateMsg("jda.invalid_config")
                }

                AuthDatabase.reload().join()

                reloading = false
                return@supplyAsync true
            }.exceptionally {
                it.printStackTrace()
                sender.sendTranslateMsg("jda.reload_error")
                reloading = false
                return@exceptionally false
            }
        }
    }

    override fun onStart() {
        Logger.getLogger("org.mongodb.driver.*").level = Level.SEVERE
        JDALogger.setFallbackLoggerEnabled(false)
        reload(Bukkit.getConsoleSender()).join()
        AuthCommands()
    }

    override fun onDisable() {
        if (jda != null) {
            val jda = jda!!
            jda.shutdown()
            if (!jda.awaitShutdown(Duration.ofSeconds(10))) {
                logger.warning("Force shutdown JDA")
                jda.shutdownNow()
            }
        }
        AuthDatabase.sDatabase.close()
    }

    override fun getAuthDataByDiscordId(discordId: Long): CompletableFuture<AuthData> {
        return AuthDatabase.getAuthDataByDiscordId(discordId)
    }

    override fun getAuthDataByMinecraftUUID(minecraftUUID: UUID): CompletableFuture<AuthData> {
        return AuthDatabase.getAuthDataByMinecraftUUID(minecraftUUID)
    }
}