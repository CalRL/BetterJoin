package me.calrl.betterjoin.Listeners;

import me.calrl.betterjoin.BetterJoin;
import me.calrl.betterjoin.Functions.FireworkCreator;
import me.calrl.betterjoin.Functions.LegacyToMiniMessageConverter;
import me.calrl.betterjoin.Functions.ParsePlaceholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class PlayerJoinListener implements Listener {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private Logger logger = BetterJoin.getInstance().getLogger();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = BetterJoin.getInstance().getConfig();

        ConfigurationSection groupsSection = config.getConfigurationSection("Groups");
        if (groupsSection != null) {
            Set<String> groups = groupsSection.getKeys(false);

            for (String group : groups) {
                String permission = groupsSection.getString(group + ".permission");
                if (permission != null && player.hasPermission(permission)) {
                    handleJoin(event, groupsSection.getConfigurationSection(group));
                    break; // Exit loop after applying the first matching group
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = BetterJoin.getInstance().getConfig();

        ConfigurationSection groupsSection = config.getConfigurationSection("Groups");
        if (groupsSection != null) {
            Set<String> groups = groupsSection.getKeys(false);

            for (String group : groups) {
                String permission = groupsSection.getString(group + ".permission");
                if (permission != null && player.hasPermission(permission)) {
                    handleLeave(event, groupsSection.getConfigurationSection(group));
                    break; // Exit loop after applying the first matching group
                }
            }
        }
    }

    private void handleJoin(PlayerJoinEvent event, ConfigurationSection groupConfig) {
        if (groupConfig.contains("join.commands")) {
            List<String> commands = groupConfig.getStringList("join.commands");
            runCommands(event.getPlayer(), commands);
        }
        if (groupConfig.getBoolean("join.message.enabled")) {
            event.setJoinMessage(null);
            Bukkit.getScheduler().runTask(BetterJoin.getInstance(), () -> {
                String joinMessage = ParsePlaceholders.parsePlaceholders(event.getPlayer(), groupConfig.getString("join.message.text"));
                String convertedMessage = LegacyToMiniMessageConverter.convertLegacyToMiniMessage(joinMessage);
                Component message = miniMessage.deserialize(convertedMessage, Placeholder.parsed("name", LegacyToMiniMessageConverter.convertLegacyToMiniMessage(event.getPlayer().getDisplayName())));
                BetterJoin.getInstance().adventure().all().sendMessage(message);
            });
        }
        if (groupConfig.getBoolean("join.firework.enabled")) {
            Bukkit.getScheduler().runTask(BetterJoin.getInstance(), () -> {
                FireworkEffect fireworkEffect = FireworkCreator.createFireworkEffect(groupConfig.getCurrentPath() + ".join.firework.type", groupConfig.getCurrentPath() + ".join.firework.color");
                spawnFirework(event.getPlayer(), fireworkEffect, groupConfig.getInt("join.firework.power", 1));
            });

        }
        if(groupConfig.getBoolean("join.title.enabled")) {
            Bukkit.getScheduler().runTask(BetterJoin.getInstance(), () -> {
               Player player = event.getPlayer();
               Audience audience = BetterJoin.getInstance().adventure().player(player);
               Component title = miniMessage.deserialize(LegacyToMiniMessageConverter.convertLegacyToMiniMessage(ParsePlaceholders.parsePlaceholders(player, groupConfig.getString("join.title.title"))));
               Component subtitle = miniMessage.deserialize(LegacyToMiniMessageConverter.convertLegacyToMiniMessage(ParsePlaceholders.parsePlaceholders(player, groupConfig.getString("join.title.subtitle"))));
               Title.Times times = Title.Times.times(
                       Duration.ofMillis(groupConfig.getInt("join.title.fadein")),
                       Duration.ofMillis(groupConfig.getInt("join.title.stay")),
                       Duration.ofMillis(groupConfig.getInt("join.title.fadeout"))
               );
               Title fullTitle = Title.title(title, subtitle, times);
               logger.info("Sending Title");

               audience.showTitle(fullTitle);
            });
        }

        // Add code here to handle fireworks or other join actions as needed
    }

    private void handleLeave(PlayerQuitEvent event, ConfigurationSection groupConfig) {
        if (groupConfig.getBoolean("leave.message.enabled")) {
            event.setQuitMessage(null);
            Bukkit.getScheduler().runTask(BetterJoin.getInstance(), () -> {
                String leaveMessage = ParsePlaceholders.parsePlaceholders(event.getPlayer(), groupConfig.getString("leave.message.text"));
                String convertedMessage = LegacyToMiniMessageConverter.convertLegacyToMiniMessage(leaveMessage);
                Component message = miniMessage.deserialize(convertedMessage, Placeholder.parsed("name", LegacyToMiniMessageConverter.convertLegacyToMiniMessage(event.getPlayer().getDisplayName())));
                BetterJoin.getInstance().adventure().all().sendMessage(message);
            });
        }
    }
    private void spawnFirework(Player player, FireworkEffect effect, int power) {
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect);
        meta.setPower(power);
        firework.setFireworkMeta(meta);
    }
    private void runCommands(Player player, List<String> commands) {
        for (String command : commands) {
            try {
                if (command.startsWith("[player] ")) {
                    String playerCommand = command.substring(9).trim();
                    logger.info("Executing player command: /" + playerCommand);
                    player.performCommand(playerCommand);
                } else if (command.startsWith("[console] ")) {
                    String consoleCommand = command.substring(10).trim().replace("<player>", player.getName());
                    logger.info("Executing console command: /" + consoleCommand);
                    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                    Bukkit.dispatchCommand(console, consoleCommand);
                }
            } catch (Exception e) {
                logger.severe("Failed to execute command: " + command);
                e.printStackTrace();
            }
        }
    }

}


