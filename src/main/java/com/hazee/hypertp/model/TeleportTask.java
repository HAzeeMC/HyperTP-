package com.hazee.hypertp.model;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

/**
 * Represents a teleport task with countdown and movement detection
 * Used for delayed teleportation with safety checks
 */
public class TeleportTask {
    
    private final Player player;
    private final Location targetLocation;
    private final Location initialLocation;
    private final int delay;
    private final String teleportType;
    private final Consumer<Boolean> callback;
    
    private BukkitRunnable countdownTask;
    private boolean cancelled = false;
    private int remainingTime;
    
    public TeleportTask(Player player, Location targetLocation, int delay, String teleportType, Consumer<Boolean> callback) {
        this.player = player;
        this.targetLocation = targetLocation;
        this.delay = delay;
        this.teleportType = teleportType;
        this.callback = callback;
        this.initialLocation = player.getLocation().clone();
        this.remainingTime = delay;
    }
    
    /**
     * Start the teleport countdown
     */
    public void start() {
        if (delay <= 0) {
            executeTeleport();
            return;
        }
        
        this.countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelTeleport(false);
                    return;
                }
                
                // Check if player moved
                if (hasPlayerMoved()) {
                    cancelTeleport(false);
                    return;
                }
                
                if (remainingTime <= 0) {
                    executeTeleport();
                    return;
                }
                
                remainingTime--;
            }
        };
        
        // Start the countdown task
        countdownTask.runTaskTimer(com.hazee.hypertp.HyperTP.getInstance(), 0L, 20L);
    }
    
    /**
     * Check if player has moved from initial position
     */
    private boolean hasPlayerMoved() {
        Location current = player.getLocation();
        return initialLocation.getBlockX() != current.getBlockX() ||
               initialLocation.getBlockY() != current.getBlockY() ||
               initialLocation.getBlockZ() != current.getBlockZ();
    }
    
    /**
     * Execute the teleport
     */
    private void executeTeleport() {
        if (cancelled) return;
        
        try {
            boolean success = player.teleport(targetLocation);
            if (callback != null) {
                callback.accept(success);
            }
            
            if (countdownTask != null && !countdownTask.isCancelled()) {
                countdownTask.cancel();
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.accept(false);
            }
        }
    }
    
    /**
     * Cancel the teleport task
     */
    public void cancelTeleport(boolean notifyPlayer) {
        this.cancelled = true;
        
        if (countdownTask != null && !countdownTask.isCancelled()) {
            countdownTask.cancel();
        }
        
        if (callback != null) {
            callback.accept(false);
        }
        
        if (notifyPlayer && player.isOnline()) {
            player.sendMessage(com.hazee.hypertp.util.ChatUtil.colorize("&cTeleport cancelled."));
        }
    }
    
    /**
     * Get remaining time in seconds
     */
    public int getRemainingTime() {
        return remainingTime;
    }
    
    /**
     * Get the teleport type
     */
    public String getTeleportType() {
        return teleportType;
    }
    
    /**
     * Check if task is cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * Check if task is completed
     */
    public boolean isCompleted() {
        return remainingTime <= 0 && !cancelled;
    }
    
    /**
     * Get the player associated with this task
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Get the target location
     */
    public Location getTargetLocation() {
        return targetLocation;
    }
}
