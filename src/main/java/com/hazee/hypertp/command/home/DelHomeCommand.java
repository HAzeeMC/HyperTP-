package com.hazee.hypertp.command.home;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.command.core.BaseCommand;
import com.hazee.hypertp.manager.HomeManager;
import org.bukkit.entity.Player;

public class DelHomeCommand extends BaseCommand {
    
    private final HomeManager homeManager;
    
    public DelHomeCommand(HyperTP plugin) {
        super(plugin);
        this.homeManager = plugin.getHomeManager();
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(configManager.getLang("delhome-usage"));
            return true;
        }
        
        String homeName = args[0].toLowerCase();
        
        if (!homeManager.hasHome(player.getUniqueId(), homeName)) {
            player.sendMessage(configManager.getLang("home-not-exist"));
            return true;
        }
        
        homeManager.deleteHome(player.getUniqueId(), homeName);
        player.sendMessage(configManager.getLang("home-deleted").replace("%home%", homeName));
        
        return true;
    }
    
    @Override
    protected String getPermission() {
        return "hypertp.delhome";
    }
}