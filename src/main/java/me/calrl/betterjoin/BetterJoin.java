package me.calrl.betterjoin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public final class BetterJoin extends JavaPlugin {
    private final Logger logger = getLogger();
    public void loadComponents() {

    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        loadComponents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public BetterJoin getInstance() { return this; }
}
