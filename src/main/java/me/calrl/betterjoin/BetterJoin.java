package me.calrl.betterjoin;
import me.calrl.betterjoin.Listeners.PlayerJoinListener;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public final class BetterJoin extends JavaPlugin {
    private final Logger logger = getLogger();
    private static BetterJoin instance;
    private BukkitAudiences adventure;

    public void loadComponents() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }
    @Override
    public void onEnable() {
        instance = this;
        adventure = BukkitAudiences.create(this);
        // Plugin startup logic
        this.saveDefaultConfig();
        loadComponents();
        logger.info("Loaded.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    public void reloadPlugin() {
        logger.info("Reloading BetterJoin...");
        // Reload the configuration
        reloadConfig();

        // Reinitialize components
        loadComponents();

        logger.info("BetterJoin has been reloaded!");
    }
    public BukkitAudiences adventure() {
        return adventure;
    }

    public static BetterJoin getInstance() { return instance; }
}
