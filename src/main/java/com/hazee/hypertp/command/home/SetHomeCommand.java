package com.hazee.hypertp.command.home;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.command.core.BaseCommand;
import com.hazee.hypertp.manager.HomeManager;
import com.hazee.hypertp.model.Home;
import org.bukkit.entity.Player;

public class SetHomeCommand extends BaseCommand {
    
    private final HomeManager homeManager;
    
    public SetHomeCommand(HyperTP plugin) {
        super(plugin);
        this.homeManager = plugin.getHomeManager();
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(configManager.getLang("sethome-usage"));
            return true;
        }
        
        String homeName = args[0].toLowerCase();
        
        if (!isValidHomeName(homeName)) {
            player.sendMessage(configManager.getLang("invalid-home-name"));
            return true;
        }
        
        int maxHomes = getMaxHomes(player);
        int currentHomes = homeManager.getPlayerHomes(player.getUniqueId()).size();
        
        if (currentHomes >= maxHomes && !homeManager.hasHome(player.getUniqueId(), homeName)) {
            player.sendMessage(configManager.getLang("max-homes-reached").replace("%max%", String.valueOf(maxHomes)));
            return true;
        }
        
        homeManager.setHome(player.getUniqueId(), new Home(player.getUniqueId(), homeName, player.getLocation()));
        player.sendMessage(configManager.getLang("home-set").replace("%home%", homeName));
        
        return true;
    }
    
    private boolean isValidHomeName(String name) {
        return name.matches("[a-zA-Z0-9_]{1,16}");
    }
    
    private int getMaxHomes(Player player) {
        for (int i = 10; i >= 1; i--) {
            if (player.hasPermission("hypertp.homes." + i)) {
                return i;
            }
        }
        return configManager.getConfig().getInt("max-homes.default", 3);
    }
    
    @Override
    protected String getPermission() {
        return "hypertp.sethome";
    }
}