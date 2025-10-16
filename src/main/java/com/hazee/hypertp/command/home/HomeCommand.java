package com.hazee.hypertp.command.home;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.command.core.BaseCommand;
import com.hazee.hypertp.manager.CooldownManager;
import com.hazee.hypertp.manager.HomeManager;
import com.hazee.hypertp.model.Home;
import com.hazee.hypertp.task.TeleportCountdownTask;
import org.bukkit.entity.Player;

public class HomeCommand extends BaseCommand {
    
    private final HomeManager homeManager;
    private final CooldownManager cooldownManager;
    
    public HomeCommand(HyperTP plugin) {
        super(plugin);
        this.homeManager = plugin.getHomeManager();
        this.cooldownManager = plugin.getCooldownManager();
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            plugin.getGuiHandler().openHomeGUI(player);
            return true;
        }
        
        String homeName = args[0].toLowerCase();
        
        if (!homeManager.hasHome(player.getUniqueId(), homeName)) {
            player.sendMessage(configManager.getLang("home-not-exist"));
            return true;
        }
        
        Home home = homeManager.getHome(player.getUniqueId(), homeName);
        
        if (cooldownManager.hasCooldown(player.getUniqueId(), "home")) {
            long remaining = cooldownManager.getRemainingCooldown(player.getUniqueId(), "home");
            player.sendMessage(configManager.getLang("cooldown-home").replace("%time%", String.valueOf(remaining)));
            return true;
        }
        
        int delay = configManager.getConfig().getInt("teleport-delay.home", 3);
        
        if (delay > 0) {
            player.sendMessage(configManager.getLang("teleport-start").replace("%time%", String.valueOf(delay)));
            TeleportCountdownTask task = new TeleportCountdownTask(plugin, player, home.getLocation(), delay, "home");
            task.start();
        } else {
            plugin.getFoliaScheduler().teleportAsync(player, home.getLocation()).thenAccept(success -> {
                if (success) {
                    player.sendMessage(configManager.getLang("teleport-success-home").replace("%home%", homeName));
                    
                    int cooldown = configManager.getConfig().getInt("cooldowns.home", 0);
                    if (cooldown > 0) {
                        cooldownManager.setCooldown(player.getUniqueId(), "home", cooldown);
                    }
                } else {
                    player.sendMessage("&cTeleport failed!");
                }
            });
        }
        
        return true;
    }
    
    @Override
    protected String getPermission() {
        return "hypertp.home";
    }
}