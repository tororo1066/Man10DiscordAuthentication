package tororo1066.man10discordauthentication;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Man10DiscordAuthenticationAPI {
    /**
     * Get AuthData by Discord ID
     * @param discordId Discord ID(Long)
     * @return CompletableFuture<AuthData>
     * @see AuthData
     */
    CompletableFuture<AuthData> getAuthDataByDiscordId(@NotNull Long discordId);

    /**
     * Get AuthData by Minecraft UUID
     * @param minecraftUUID Minecraft UUID(UUID)
     * @return CompletableFuture<AuthData>
     * @see AuthData
     */
    CompletableFuture<AuthData> getAuthDataByMinecraftUUID(@NotNull UUID minecraftUUID);
}
