package com.hazee.hypertp.command.teleport;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.command.core.BaseCommand;
import com.hazee.hypertp.manager.CooldownManager;
import com.hazee.hypertp.task.TeleportCountdownTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackCommand extends BaseCommand {
    
    private final CooldownManager cooldownManager;
    private final Map<UUID, Location> lastLocations;
    
    public BackCommand(HyperTP plugin) {
        super(plugin);
        this.cooldownManager = plugin.getCooldownManager();
        this.lastLocations = new HashMap<>();
    }
    
    public void setLastLocation(Player player) {
        lastLocations.put(player.getUniqueId(), player.getLocation());
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        Location lastLocation = lastLocations.get(player.getUniqueId());
        
        if (lastLocation == null) {
            player.sendMessage(configManager.getLang("no-back-location"));
            return true;
        }
        
        if (cooldownManager.hasCooldown(player.getUniqueId(), "back")) {
            long remaining = cooldownManager.getRemainingCooldown(player.getUniqueId(), "back");
            player.sendMessage(configManager.getLang("cooldown-back").replace("%time%", String.valueOf(remaining)));
            return true;
        }
        
        int delay = configManager.getConfig().getInt("teleport-delay.back", 3);
        
        if (delay > 0) {
            player.sendMessage(configManager.getLang("teleport-start").replace("%time%", String.valueOf(delay)));
            new TeleportCountdownTask(plugin, player, lastLocation, delay, "back").runTaskTimer(plugin, 0L, 20L);
        } else {
            player.teleport(lastLocation);
            player.sendMessage(configManager.getLang("teleport-success-back"));
            
            int cooldown = configManager.getConfig().getInt("cooldowns.back", 0);
            if (cooldown > 0) {
                cooldownManager.setCooldown(player.getUniqueId(), "back", cooldown);
            }
        }
        
        lastLocations.remove(player.getUniqueId());
        return true;
    }
    
    @Override
    protected String getPermission() {
        return "hypertp.back";
    }
}