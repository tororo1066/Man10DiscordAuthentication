API利用方法
```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/tororo1066/Man10DiscordAuthentication")
        credentials {
            username = "<GITHUB_USERNAME(環境変数などを用いるべき)>" // System.getenv("GITHUB_USERNAME")
            password = "<GITHUB_TOKEN(環境変数などを用いるべき)>" // System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    compileOnly 'tororo1066:man10-discord-authentication:<VERSION>'
}
```

```java
import tororo1066.man10discordauthentication.Man10DiscordAuthenticationAPI;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class Example extends JavaPlugin implements Listener {
    
    public static Man10DiscordAuthenticationAPI api;
    
    @Override
    public void onEnable() {
        var plugin = Bukkit.getPluginManager().getPlugin("Man10DiscordAuthentication");
        if (plugin != null) {
            api = (Man10DiscordAuthenticationAPI) plugin;
        }
        
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var uuid = player.getUniqueId();
        
        api.getAuthDataByMinecraftUUID(uuid).thenAccept(authData -> {
            if (authData == null) {
                player.sendMessage("§c未認証です");
                return;
            }
            
            player.sendMessage("§a認証済みです");
            player.sendMessage("§aDiscordID: " + authData.getDiscordID());
        });
    }
}
```
