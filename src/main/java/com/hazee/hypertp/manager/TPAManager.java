package com.hazee.hypertp.manager;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.model.TPARequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPAManager {
    
    private final HyperTP plugin;
    private final Map<UUID, TPARequest> pendingRequests;
    
    public TPAManager(HyperTP plugin) {
        this.plugin = plugin;
        this.pendingRequests = new HashMap<>();
    }
    
    public void addRequest(TPARequest request) {
        pendingRequests.put(request.getTarget(), request);
        
        // Folia-compatible timeout (5 minutes)
        plugin.getFoliaScheduler().runTaskLater(() -> {
            if (pendingRequests.containsKey(request.getTarget())) {
                TPARequest expiredRequest = pendingRequests.remove(request.getTarget());
                
                // Notify players if online
                Player requester = Bukkit.getPlayer(expiredRequest.getRequester());
                Player target = Bukkit.getPlayer(expiredRequest.getTarget());
                
                if (requester != null && requester.isOnline()) {
                    plugin.getFoliaScheduler().runAtEntity(requester, () -> {
                        requester.sendMessage(plugin.getConfigManager().getLang("tpa-expired"));
                    });
                }
                
                if (target != null && target.isOnline()) {
                    plugin.getFoliaScheduler().runAtEntity(target, () -> {
                        target.sendMessage(plugin.getConfigManager().getLang("tpa-expired"));
                    });
                }
            }
        }, 6000L); // 5 minutes (6000 ticks)
    }
    
    public TPARequest getRequest(UUID targetUUID) {
        TPARequest request = pendingRequests.get(targetUUID);
        if (request != null && System.currentTimeMillis() - request.getTimestamp() > 300000) { // 5 minutes
            pendingRequests.remove(targetUUID);
            return null;
        }
        return request;
    }
    
    public void removeRequest(UUID targetUUID) {
        pendingRequests.remove(targetUUID);
    }
    
    public boolean hasPendingRequest(UUID targetUUID) {
        return getRequest(targetUUID) != null;
    }
}