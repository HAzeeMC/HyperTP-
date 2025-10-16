package com.hazee.hypertp.command.core;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.manager.ConfigManager;
import com.hazee.hypertp.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand implements CommandExecutor {
    
    protected final HyperTP plugin;
    protected final ConfigManager configManager;
    
    public BaseCommand(HyperTP plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.colorize("&cThis command can only be executed by players."));
            return true;
        }
        
        Player player = (Player) sender;
        String permission = getPermission();
        
        if (permission != null && !player.hasPermission(permission)) {
            player.sendMessage(configManager.getLang("no-permission"));
            return true;
        }
        
        return execute(player, args);
    }
    
    protected abstract boolean execute(Player player, String[] args);
    protected abstract String getPermission();
}