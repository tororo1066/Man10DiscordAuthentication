package tororo1066.man10discordauthentication

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

class DiscordEventListener: ListenerAdapter() {

    override fun onModalInteraction(e: ModalInteractionEvent) {
        if (e.guild == null) return
        if (e.modalId == "mda_auth") {
            if (Man10DiscordAuthentication.reloading) {
                e.reply("少し待ってからもう一度お試しください/Please wait a moment and try again").setEphemeral(true).queue()
                return
            }
            if (Man10DiscordAuthentication.processing.contains(e.user.idLong)) {
                e.reply("処理中です/Processing").setEphemeral(true).queue()
                return
            }
            e.deferReply().setEphemeral(true).queue()
            val authCode = (e.getValue("auth")?.asString ?: return).toIntOrNull()
            if (authCode == null) {
                e.hook.editOriginal("認証コードが無効です/Invalid authentication code").queue()
                return
            }

            val minecraftUUID = Man10DiscordAuthentication.authMap.remove(authCode) ?: run {
                e.hook.editOriginal("認証コードが無効です/Invalid authentication code").queue()
                return
            }

            Man10DiscordAuthentication.processing.add(e.user.idLong)
            CompletableFuture.runAsync {
                val exists = AuthDatabase.getAuthDataByDiscordId(e.user.idLong).join() != null
                if (exists) {
                    e.hook.editOriginal("既に認証されています/Already authenticated").queue()
                    return@runAsync
                }

                val name = Bukkit.getOfflinePlayer(minecraftUUID).name ?: return@runAsync

                if (AuthDatabase.insert(e.user.idLong, minecraftUUID, name).join()) {
                    e.hook.editOriginal("認証が完了しました/Authentication completed").queue()
                    e.guild?.let { guild ->
                        val role = guild.getRoleById(DiscordConfig.verifiedRole) ?: return@let
                        guild.addRoleToMember(e.user, role).queue()
                    }
                } else {
                    e.hook.editOriginal("認証に失敗しました 運営にお問い合わせください/Authentication failed. Please contact the management").queue()
                }
            }.exceptionally {
                it.printStackTrace()
                e.hook.editOriginal("エラーが発生しました/An error occurred").queue()
                null
            }.thenRun {
                Man10DiscordAuthentication.processing.remove(e.user.idLong)
            }
        }
    }

    override fun onButtonInteraction(e: ButtonInteractionEvent) {
        if (e.guild == null) return
        if (e.componentId == "mda_auth") {
            if (Man10DiscordAuthentication.reloading) {
                e.reply("少し待ってからもう一度お試しください/Please wait a moment and try again").setEphemeral(true).queue()
                return
            }
            e.replyModal(Modal.create("mda_auth", "認証/Authentication").addActionRow(
                TextInput.create("auth", "認証コード/Authentication code", TextInputStyle.SHORT)
                    .setPlaceholder("認証コード/Authentication code")
                    .setRequired(true)
                    .setRequiredRange(6, 6)
                    .build()
            ).build()).queue()
        }
    }

    override fun onSlashCommandInteraction(e: SlashCommandInteractionEvent) {
        if (e.guild == null) return
        if (e.name == "init") {
            e.channel.sendMessage(DiscordConfig.message)
                .addActionRow(
                    Button.primary("mda_auth", "認証/Authentication")
                )
                .queue()
            e.reply("初期化完了").setEphemeral(true).queue()
        }
    }
}