package com.hazee.hypertp.manager;

import com.hazee.hypertp.HyperTP;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    
    private final HyperTP plugin;
    private final Map<UUID, Map<String, Long>> cooldowns;
    
    public CooldownManager(HyperTP plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }
    
    public void setCooldown(UUID playerUUID, String type, int seconds) {
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(playerUUID, k -> new HashMap<>());
        playerCooldowns.put(type, System.currentTimeMillis() + (seconds * 1000L));
    }
    
    public boolean hasCooldown(UUID playerUUID, String type) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerUUID);
        if (playerCooldowns == null || !playerCooldowns.containsKey(type)) {
            return false;
        }
        
        long cooldownTime = playerCooldowns.get(type);
        if (System.currentTimeMillis() >= cooldownTime) {
            playerCooldowns.remove(type);
            return false;
        }
        
        return true;
    }
    
    public long getRemainingCooldown(UUID playerUUID, String type) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerUUID);
        if (playerCooldowns == null || !playerCooldowns.containsKey(type)) {
            return 0;
        }
        
        long cooldownTime = playerCooldowns.get(type);
        long remaining = (cooldownTime - System.currentTimeMillis()) / 1000;
        
        if (remaining <= 0) {
            playerCooldowns.remove(type);
            return 0;
        }
        
        return remaining;
    }
    
    public void removeCooldown(UUID playerUUID, String type) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerUUID);
        if (playerCooldowns != null) {
            playerCooldowns.remove(type);
        }
    }
}