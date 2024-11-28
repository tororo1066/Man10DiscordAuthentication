package tororo1066.man10discordauthentication;

import java.util.UUID;

/**
 * AuthData
 * @see Man10DiscordAuthenticationAPI
 */
public record AuthData(long discordId, UUID minecraftUUID) {
}
