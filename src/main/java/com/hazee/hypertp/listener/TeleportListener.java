package com.hazee.hypertp.listener;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.command.teleport.BackCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {
    
    private final HyperTP plugin;
    
    public TeleportListener(HyperTP plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        BackCommand backCommand = (BackCommand) plugin.getServer().getPluginCommand("back").getExecutor();
        
        if (backCommand != null) {
            backCommand.setLastLocation(player);
        }
    }
}