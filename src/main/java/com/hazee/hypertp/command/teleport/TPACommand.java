package com.hazee.hypertp.command.teleport;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.command.core.BaseCommand;
import com.hazee.hypertp.manager.TPAManager;
import com.hazee.hypertp.model.TPARequest;
import com.hazee.hypertp.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TPACommand extends BaseCommand {
    
    private final TPAManager tpaManager;
    
    public TPACommand(HyperTP plugin) {
        super(plugin);
        this.tpaManager = plugin.getTpaManager();
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            plugin.getGuiHandler().openTPAGUI(player);
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(configManager.getLang("player-offline"));
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage(configManager.getLang("cannot-tpa-self"));
            return true;
        }
        
        boolean isTpaHere = "tpahere".equalsIgnoreCase(player.getServer().getPluginCommand("tpahere").getName());
        
        TPARequest request = new TPARequest(
            player.getUniqueId(),
            target.getUniqueId(),
            System.currentTimeMillis(),
            isTpaHere
        );
        
        tpaManager.addRequest(request);
        
        String requestMessage = isTpaHere ? 
            configManager.getLang("tpahere-request-sent") : 
            configManager.getLang("tpa-request-sent");
        player.sendMessage(requestMessage.replace("%player%", target.getName()));
        
        String targetMessage = isTpaHere ? 
            configManager.getLang("tpahere-request-received") : 
            configManager.getLang("tpa-request-received");
        target.sendMessage(targetMessage.replace("%player%", player.getName()));
        
        return true;
    }
    
    @Override
    protected String getPermission() {
        return "hypertp.tpa";
    }
}