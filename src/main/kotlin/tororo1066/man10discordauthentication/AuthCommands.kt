package tororo1066.man10discordauthentication

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.lang.SLang.Companion.sendTranslateMsg
import tororo1066.tororopluginapi.lang.SLang.Companion.translate
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg

class AuthCommands: SCommand(
    "discord",
    perm = "man10discordauthentication.user",
    alias = listOf("auth")
) {

    @SCommandBody
    val auth = command().setPlayerFunction { sender, _, _, _ ->
        if (Man10DiscordAuthentication.reloading) {
            sender.sendTranslateMsg("auth.reloading")
            return@setPlayerFunction
        }

        var number: Int
        do {
            number = (100000..999999).random()
        } while (Man10DiscordAuthentication.authMap.containsKey(number))

        Man10DiscordAuthentication.authMap[number] = sender.uniqueId

        sender.sendMessage(
            Component.text(translate("auth.send_code", sender, sender.name, number.toString()))
                .hoverEvent(Component.text(translate("auth.send_code_hover", sender)))
                .clickEvent(ClickEvent.copyToClipboard(number.toString()))
        )
    }

    @SCommandBody(permission = "man10discordauthentication.op")
    val reload = command().addArg(SCommandArg("reload")).setNormalFunction { sender, _, _, _ ->
        if (Man10DiscordAuthentication.reloading) {
            sender.sendTranslateMsg("reload.already_reloading")
            return@setNormalFunction
        }

        Man10DiscordAuthentication.reload(sender).thenAccept {
            if (it) {
                sender.sendTranslateMsg("reload.success")
            } else {
                sender.sendTranslateMsg("reload.failed")
            }
        }
    }
}