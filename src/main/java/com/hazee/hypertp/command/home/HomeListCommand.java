package com.hazee.hypertp.command.home;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.command.core.BaseCommand;
import org.bukkit.entity.Player;

public class HomeListCommand extends BaseCommand {
    
    public HomeListCommand(HyperTP plugin) {
        super(plugin);
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "gui")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "gui");
            player.sendMessage(configManager.getLang("cooldown-gui").replace("%time%", String.valueOf(remaining)));
            return true;
        }
        
        plugin.getGuiHandler().openHomeGUI(player);
        
        int cooldown = configManager.getConfig().getInt("cooldowns.gui", 1);
        if (cooldown > 0) {
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), "gui", cooldown);
        }
        
        return true;
    }
    
    @Override
    protected String getPermission() {
        return "hypertp.homelist";
    }
}