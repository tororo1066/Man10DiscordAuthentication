package tororo1066.man10discordauthentication

import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.database.SDBCondition
import tororo1066.tororopluginapi.database.SDBVariable
import tororo1066.tororopluginapi.database.SDatabase
import java.util.UUID
import java.util.concurrent.CompletableFuture

object AuthDatabase: Man10DiscordAuthenticationAPI {
    private const val TABLE_NAME = "man10_discord_user_data"
    lateinit var sDatabase: SDatabase

    fun reload(): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            try {
                if (::sDatabase.isInitialized) {
                    sDatabase.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            sDatabase = SDatabase.newInstance(SJavaPlugin.plugin, null, null)
            sDatabase.createTable(TABLE_NAME, mapOf(
                "id" to SDBVariable(SDBVariable.Int, autoIncrement = true),
                "discord_id" to SDBVariable(SDBVariable.BigInt),
                "minecraft_uuid" to SDBVariable(SDBVariable.VarChar, 36),
                "minecraft_name" to SDBVariable(SDBVariable.VarChar, 16)
            ))
        }.exceptionally {
            it.printStackTrace()
            false
        }
    }

    fun insert(discordId: Long, minecraftUuid: UUID, minecraftName: String): CompletableFuture<Boolean> {
        return sDatabase.asyncInsert(TABLE_NAME, mapOf(
            "discord_id" to discordId,
            "minecraft_uuid" to minecraftUuid.toString(),
            "minecraft_name" to minecraftName
        ))
    }

    override fun getAuthDataByDiscordId(discordId: Long): CompletableFuture<AuthData> {
        return sDatabase.asyncSelect(TABLE_NAME, SDBCondition().equal("discord_id", discordId)).thenApplyAsync {
            if (it.isEmpty()) return@thenApplyAsync null
            val row = it.first()
            AuthData(row.getLong("discord_id"), UUID.fromString(row.getString("minecraft_uuid")))
        }
    }

    override fun getAuthDataByMinecraftUUID(minecraftUUID: UUID): CompletableFuture<AuthData> {
        return sDatabase.asyncSelect(TABLE_NAME, SDBCondition().equal("minecraft_uuid", minecraftUUID.toString())).thenApplyAsync {
            if (it.isEmpty()) return@thenApplyAsync null
            val row = it.first()
            AuthData(row.getLong("discord_id"), UUID.fromString(row.getString("minecraft_uuid")))
        }
    }


}