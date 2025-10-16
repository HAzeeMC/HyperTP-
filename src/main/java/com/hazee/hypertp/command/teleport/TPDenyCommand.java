package com.hazee.hypertp.command.teleport;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.command.core.BaseCommand;
import com.hazee.hypertp.manager.TPAManager;
import com.hazee.hypertp.model.TPARequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TPDenyCommand extends BaseCommand {
    
    private final TPAManager tpaManager;
    
    public TPDenyCommand(HyperTP plugin) {
        super(plugin);
        this.tpaManager = plugin.getTpaManager();
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        TPARequest request = tpaManager.getRequest(player.getUniqueId());
        
        if (request == null) {
            player.sendMessage(configManager.getLang("no-pending-request"));
            return true;
        }
        
        Player requester = Bukkit.getPlayer(request.getRequester());
        if (requester != null) {
            requester.sendMessage(configManager.getLang("request-denied"));
        }
        
        player.sendMessage(configManager.getLang("request-denied"));
        tpaManager.removeRequest(player.getUniqueId());
        
        return true;
    }
    
    @Override
    protected String getPermission() {
        return "hypertp.tpdeny";
    }
}