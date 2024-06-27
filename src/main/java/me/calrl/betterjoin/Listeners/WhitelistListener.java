package me.calrl.betterjoin.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class WhitelistListener implements Listener {
    @EventHandler
    private void onWhitelistToggle(PlayerCommandPreprocessEvent event) {
        event.getMessage();
    }
}
