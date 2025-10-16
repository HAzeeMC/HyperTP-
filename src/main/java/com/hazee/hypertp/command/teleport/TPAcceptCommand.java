package com.hazee.hypertp.command.teleport;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.command.core.BaseCommand;
import com.hazee.hypertp.manager.TPAManager;
import com.hazee.hypertp.model.TPARequest;
import com.hazee.hypertp.task.TeleportCountdownTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TPAcceptCommand extends BaseCommand {
    
    private final TPAManager tpaManager;
    
    public TPAcceptCommand(HyperTP plugin) {
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
        if (requester == null) {
            player.sendMessage(configManager.getLang("player-offline"));
            tpaManager.removeRequest(player.getUniqueId());
            return true;
        }
        
        int delay = configManager.getConfig().getInt("teleport-delay.tpa", 3);
        Player teleportingPlayer = request.isTpaHere() ? requester : player;
        Player targetPlayer = request.isTpaHere() ? player : requester;
        
        if (delay > 0) {
            teleportingPlayer.sendMessage(configManager.getLang("teleport-start").replace("%time%", String.valueOf(delay)));
            new TeleportCountdownTask(plugin, teleportingPlayer, targetPlayer.getLocation(), delay, "tpa").runTaskTimer(plugin, 0L, 20L);
        } else {
            teleportingPlayer.teleport(targetPlayer.getLocation());
            teleportingPlayer.sendMessage(configManager.getLang("teleport-success-tpa"));
        }
        
        requester.sendMessage(configManager.getLang("request-accepted"));
        player.sendMessage(configManager.getLang("request-accepted"));
        
        tpaManager.removeRequest(player.getUniqueId());
        return true;
    }
    
    @Override
    protected String getPermission() {
        return "hypertp.tpaccept";
    }
}