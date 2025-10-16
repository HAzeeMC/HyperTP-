package com.hazee.hypertp.task;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.manager.CooldownManager;
import com.hazee.hypertp.util.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportCountdownTask extends BukkitRunnable {
    
    private final HyperTP plugin;
    private final Player player;
    private final Location targetLocation;
    private final String teleportType;
    private final Location initialLocation;
    private int countdown;
    private final int initialCountdown;
    private BukkitTask task;
    
    private static final Map<UUID, TeleportCountdownTask> activeTeleports = new HashMap<>();
    
    public TeleportCountdownTask(HyperTP plugin, Player player, Location targetLocation, int countdown, String teleportType) {
        this.plugin = plugin;
        this.player = player;
        this.targetLocation = targetLocation;
        this.countdown = countdown;
        this.initialCountdown = countdown;
        this.teleportType = teleportType;
        this.initialLocation = player.getLocation().clone();
        
        // Cancel any existing teleport for this player
        cancelExistingTeleport(player.getUniqueId());
        activeTeleports.put(player.getUniqueId(), this);
    }
    
    public void start() {
        if (plugin.isFolia()) {
            // Folia: Use entity scheduler
            this.task = plugin.getFoliaScheduler().runAtEntity(player, this);
        } else {
            // Paper: Normal task timer
            this.task = this.runTaskTimer(plugin, 0L, 20L);
        }
    }
    
    @Override
    public void run() {
        if (!player.isOnline()) {
            cancelTeleport("&cTeleport cancelled: Player went offline");
            return;
        }
        
        // Check if player moved
        if (hasPlayerMoved()) {
            cancelTeleport(plugin.getConfigManager().getLang("teleport-cancelled"));
            return;
        }
        
        if (countdown <= 0) {
            completeTeleport();
            return;
        }
        
        // Send countdown message for last 3 seconds
        if (countdown <= 3) {
            ChatUtil.sendMessage(player, 
                plugin.getConfigManager().getLang("teleport-countdown")
                    .replace("%time%", String.valueOf(countdown))
            );
        }
        
        countdown--;
    }
    
    private boolean hasPlayerMoved() {
        Location currentLocation = player.getLocation();
        return initialLocation.getBlockX() != currentLocation.getBlockX() ||
               initialLocation.getBlockY() != currentLocation.getBlockY() ||
               initialLocation.getBlockZ() != currentLocation.getBlockZ();
    }
    
    private void completeTeleport() {
        plugin.getFoliaScheduler().teleportAsync(player, targetLocation).thenAccept(success -> {
            if (success) {
                switch (teleportType) {
                    case "home":
                        ChatUtil.sendMessage(player, plugin.getConfigManager().getLang("teleport-success-home"));
                        break;
                    case "back":
                        ChatUtil.sendMessage(player, plugin.getConfigManager().getLang("teleport-success-back"));
                        break;
                    case "tpa":
                        ChatUtil.sendMessage(player, plugin.getConfigManager().getLang("teleport-success-tpa"));
                        break;
                    default:
                        ChatUtil.sendMessage(player, "&aTeleport successful!");
                        break;
                }
                
                setCooldown();
            } else {
                ChatUtil.sendMessage(player, "&cTeleport failed!");
            }
            cleanup();
        });
    }
    
    private void cancelTeleport(String message) {
        if (message != null && !message.isEmpty()) {
            ChatUtil.sendMessage(player, message);
        }
        cleanup();
    }
    
    private void setCooldown() {
        CooldownManager cooldownManager = plugin.getCooldownManager();
        int cooldown = plugin.getConfigManager().getConfig().getInt("cooldowns." + teleportType, 0);
        
        if (cooldown > 0) {
            cooldownManager.setCooldown(player.getUniqueId(), teleportType, cooldown);
        }
    }
    
    private void cleanup() {
        activeTeleports.remove(player.getUniqueId());
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
    
    private void cancelExistingTeleport(UUID playerUUID) {
        TeleportCountdownTask existing = activeTeleports.get(playerUUID);
        if (existing != null) {
            existing.cancelTeleport(null);
        }
    }
    
    public static boolean hasActiveTeleport(UUID playerUUID) {
        return activeTeleports.containsKey(playerUUID);
    }
    
    public static void cancelPlayerTeleport(UUID playerUUID) {
        TeleportCountdownTask task = activeTeleports.get(playerUUID);
        if (task != null) {
            task.cancelTeleport("&cTeleport cancelled by admin");
        }
    }
    
    public int getRemainingTime() {
        return countdown;
    }
    
    public String getTeleportType() {
        return teleportType;
    }
}