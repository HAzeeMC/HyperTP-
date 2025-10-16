package com.hazee.hypertp.task;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.manager.CooldownManager;
import com.hazee.hypertp.model.TeleportTask;
import com.hazee.hypertp.util.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Enhanced teleport countdown task with movement detection and Folia support
 * Uses the TeleportTask model for better organization
 */
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
    
    /**
     * Start the countdown task with Folia support
     */
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
    
    /**
     * Check if player has moved significantly
     */
    private boolean hasPlayerMoved() {
        if (!plugin.getConfigManager().getConfig().getBoolean("security.check-movement", true)) {
            return false;
        }
        
        Location currentLocation = player.getLocation();
        return initialLocation.getBlockX() != currentLocation.getBlockX() ||
               initialLocation.getBlockY() != currentLocation.getBlockY() ||
               initialLocation.getBlockZ() != currentLocation.getBlockZ();
    }
    
    /**
     * Complete the teleport process
     */
    private void completeTeleport() {
        plugin.getFoliaScheduler().teleportAsync(player, targetLocation).thenAccept(success -> {
            if (success) {
                sendSuccessMessage();
                setCooldown();
                
                // Play success sound if configured
                playTeleportSound("teleport-success");
            } else {
                ChatUtil.sendMessage(player, "&cTeleport failed!");
                playTeleportSound("teleport-failed");
            }
            cleanup();
        });
    }
    
    /**
     * Send appropriate success message based on teleport type
     */
    private void sendSuccessMessage() {
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
            case "rtp":
                ChatUtil.sendMessage(player, plugin.getConfigManager().getLang("rtp-success"));
                break;
            default:
                ChatUtil.sendMessage(player, "&aTeleport successful!");
                break;
        }
    }
    
    /**
     * Play teleport sound if configured
     */
    private void playTeleportSound(String soundType) {
        if (!plugin.getConfigManager().getConfig().getBoolean("sounds.enabled", true)) {
            return;
        }
        
        String soundName = plugin.getConfigManager().getConfig().getString("sounds." + soundType);
        if (soundName != null && !soundName.isEmpty()) {
            try {
                org.bukkit.Sound sound = org.bukkit.Sound.valueOf(soundName.toUpperCase());
                plugin.getFoliaScheduler().runAtEntity(player, () -> {
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                });
            } catch (IllegalArgumentException e) {
                // Sound not found, ignore
            }
        }
    }
    
    /**
     * Cancel the teleport with message
     */
    private void cancelTeleport(String message) {
        if (message != null && !message.isEmpty()) {
            ChatUtil.sendMessage(player, message);
        }
        playTeleportSound("teleport-failed");
        cleanup();
    }
    
    /**
     * Set cooldown for the teleport type
     */
    private void setCooldown() {
        CooldownManager cooldownManager = plugin.getCooldownManager();
        int cooldown = plugin.getConfigManager().getConfig().getInt("cooldowns." + teleportType, 0);
        
        if (cooldown > 0) {
            cooldownManager.setCooldown(player.getUniqueId(), teleportType, cooldown);
        }
    }
    
    /**
     * Clean up resources
     */
    private void cleanup() {
        activeTeleports.remove(player.getUniqueId());
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
    
    /**
     * Cancel existing teleport for player
     */
    private void cancelExistingTeleport(UUID playerUUID) {
        TeleportCountdownTask existing = activeTeleports.get(playerUUID);
        if (existing != null) {
            existing.cancelTeleport(null);
        }
    }
    
    // Static utility methods
    
    public static boolean hasActiveTeleport(UUID playerUUID) {
        return activeTeleports.containsKey(playerUUID);
    }
    
    public static void cancelPlayerTeleport(UUID playerUUID) {
        TeleportCountdownTask task = activeTeleports.get(playerUUID);
        if (task != null) {
            task.cancelTeleport("&cTeleport cancelled by admin");
        }
    }
    
    public static TeleportCountdownTask getPlayerTeleport(UUID playerUUID) {
        return activeTeleports.get(playerUUID);
    }
    
    // Getters
    
    public int getRemainingTime() {
        return countdown;
    }
    
    public String getTeleportType() {
        return teleportType;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Location getTargetLocation() {
        return targetLocation;
    }
    
    public boolean isActive() {
        return task != null && !task.isCancelled() && countdown > 0;
    }
}
