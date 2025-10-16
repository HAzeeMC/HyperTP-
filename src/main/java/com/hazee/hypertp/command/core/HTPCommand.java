package com.hazee.hypertp.command.core;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.util.ChatUtil;
import org.bukkit.entity.Player;

public class HTPCommand extends BaseCommand {
    
    public HTPCommand(HyperTP plugin) {
        super(plugin);
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                if (!player.hasPermission("hypertp.admin")) {
                    player.sendMessage(configManager.getLang("no-permission"));
                    return true;
                }
                plugin.getConfigManager().reloadConfigs();
                player.sendMessage(configManager.getLang("config-reloaded"));
                break;
                
            case "help":
            case "helps":
                showHelp(player);
                break;
                
            default:
                player.sendMessage(configManager.getLang("unknown-command"));
                break;
        }
        return true;
    }
    
    private void showHelp(Player player) {
        for (String line : configManager.getLangList("help-message")) {
            player.sendMessage(ChatUtil.colorize(line));
        }
    }
    
    @Override
    protected String getPermission() {
        return null;
    }
}