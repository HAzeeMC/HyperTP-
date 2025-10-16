package com.hazee.hypertp.command.teleport;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.command.core.BaseCommand;
import com.hazee.hypertp.manager.CooldownManager;
import com.hazee.hypertp.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class RTPCommand extends BaseCommand {
    
    private final CooldownManager cooldownManager;
    
    public RTPCommand(HyperTP plugin) {
        super(plugin);
        this.cooldownManager = plugin.getCooldownManager();
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        if (cooldownManager.hasCooldown(player.getUniqueId(), "rtp")) {
            long remaining = cooldownManager.getRemainingCooldown(player.getUniqueId(), "rtp");
            player.sendMessage(configManager.getLang("cooldown-rtp").replace("%time%", String.valueOf(remaining)));
            return true;
        }
        
        int minDistance = configManager.getConfig().getInt("rtp.min-distance", 100);
        int maxDistance = configManager.getConfig().getInt("rtp.max-distance", 1000);
        
        player.sendMessage(configManager.getLang("rtp-searching"));
        
        // Run RTP search asynchronously for better performance
        plugin.getFoliaScheduler().runTask(() -> {
            Location safeLocation = LocationUtil.findSafeLocation(player.getWorld(), minDistance, maxDistance);
            
            plugin.getFoliaScheduler().runAtEntity(player, () -> {
                if (safeLocation == null) {
                    player.sendMessage(configManager.getLang("rtp-failed"));
                    return;
                }
                
                plugin.getFoliaScheduler().teleportAsync(player, safeLocation).thenAccept(success -> {
                    if (success) {
                        player.sendMessage(configManager.getLang("rtp-success"));
                        
                        int cooldown = configManager.getConfig().getInt("cooldowns.rtp", 60);
                        if (cooldown > 0) {
                            cooldownManager.setCooldown(player.getUniqueId(), "rtp", cooldown);
                        }
                    } else {
                        player.sendMessage("&cRTP teleport failed!");
                    }
                });
            });
        });
        
        return true;
    }
    
    @Override
    protected String getPermission() {
        return "hypertp.rtp";
    }
}