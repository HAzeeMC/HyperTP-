package com.hazee.hypertp.listener;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.gui.core.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class GUIListener implements Listener {
    
    private final HyperTP plugin;
    
    public GUIListener(HyperTP plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        InventoryHolder holder = event.getInventory().getHolder();
        
        if (holder instanceof GUI) {
            event.setCancelled(true);
            
            GUI gui = (GUI) holder;
            gui.onClick(event.getRawSlot());
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Clean up any GUI resources if needed
    }
}