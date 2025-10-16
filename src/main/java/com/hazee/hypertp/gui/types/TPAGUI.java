package com.hazee.hypertp.gui.types;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.gui.core.GUI;
import com.hazee.hypertp.manager.TPAManager;
import com.hazee.hypertp.model.TPARequest;
import com.hazee.hypertp.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player; // CONFIRM THIS EXISTS
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class TPAGUI extends GUI {
    
    private final HyperTP plugin;
    private final TPAManager tpaManager;
    
    public TPAGUI(HyperTP plugin, Player player) {
        super(player);
        this.plugin = plugin;
        this.tpaManager = plugin.getTpaManager();
    }
    
    @Override
    public void open() {
        String title = plugin.getConfigManager().getLang("gui.tpa.title");
        int rows = plugin.getConfigManager().getConfig().getInt("gui.tpa.rows", 2);
        
        createInventory(title, rows);
        
        // Online players section
        int slot = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.equals(player)) continue;
            if (slot >= inventory.getSize() - 2) break;
            
            ItemStack playerHead = createPlayerHead(onlinePlayer);
            setItem(slot, playerHead, () -> sendTPARequest(onlinePlayer));
            slot++;
        }
        
        // TPA Here button
        ItemStack tpaHereItem = createTPAHereItem();
        setItem(inventory.getSize() - 9, tpaHereItem, () -> openTPAHereGUI());
        
        // Close button
        ItemStack closeItem = createCloseItem();
        setItem(inventory.getSize() - 1, closeItem, () -> player.closeInventory());
        
        player.openInventory(inventory);
    }
    
    @Override
    public void onClick(int slot) {
        executeAction(slot);
    }
    
    private ItemStack createPlayerHead(Player target) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        if (meta != null) {
            meta.setOwningPlayer(target);
            meta.setDisplayName(ChatUtil.colorize("&e" + target.getName()));
            
            List<String> lore = new ArrayList<>();
            lore.add("&7Click to send TPA request");
            lore.add("&7to this player.");
            
            meta.setLore(lore.stream().map(ChatUtil::colorize).collect(java.util.stream.Collectors.toList()));
            head.setItemMeta(meta);
        }
        
        return head;
    }
    
    private ItemStack createTPAHereItem() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatUtil.colorize("&6TPA Here"));
            
            List<String> lore = new ArrayList<>();
            lore.add("&7Click to open TPA Here menu");
            lore.add("&7and request players to");
            lore.add("&7teleport to you.");
            
            meta.setLore(lore.stream().map(ChatUtil::colorize).collect(java.util.stream.Collectors.toList()));
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatUtil.colorize("&cClose"));
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private void sendTPARequest(Player target) {
        player.closeInventory();
        
        TPARequest request = new com.hazee.hypertp.model.TPARequest(
            player.getUniqueId(),
            target.getUniqueId(),
            System.currentTimeMillis(),
            false
        );
        
        tpaManager.addRequest(request);
        player.sendMessage(plugin.getConfigManager().getLang("tpa-request-sent").replace("%player%", target.getName()));
        target.sendMessage(plugin.getConfigManager().getLang("tpa-request-received").replace("%player%", player.getName()));
    }
    
    private void openTPAHereGUI() {
        player.closeInventory();
        TPAHereGUI tpaHereGUI = new TPAHereGUI(plugin, player);
        tpaHereGUI.open();
    }
    
    private class TPAHereGUI extends GUI {
        
        public TPAHereGUI(HyperTP plugin, Player player) {
            super(player);
        }
        
        @Override
        public void open() {
            createInventory("&6TPA Here - Select Player", 2);
            
            int slot = 0;
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.equals(player)) continue;
                if (slot >= inventory.getSize() - 1) break;
                
                ItemStack playerHead = createPlayerHead(onlinePlayer);
                setItem(slot, playerHead, () -> sendTPAHereRequest(onlinePlayer));
                slot++;
            }
            
            // Back button
            ItemStack backItem = createBackItem();
            setItem(inventory.getSize() - 1, backItem, () -> open());
            
            player.openInventory(inventory);
        }
        
        @Override
        public void onClick(int slot) {
            executeAction(slot);
        }
        
        private ItemStack createBackItem() {
            ItemStack item = new ItemStack(Material.ARROW);
            ItemMeta meta = item.getItemMeta();
            
            if (meta != null) {
                meta.setDisplayName(ChatUtil.colorize("&6Back to TPA Menu"));
                item.setItemMeta(meta);
            }
            
            return item;
        }
        
        private void sendTPAHereRequest(Player target) {
            player.closeInventory();
            
            TPARequest request = new com.hazee.hypertp.model.TPARequest(
                player.getUniqueId(),
                target.getUniqueId(),
                System.currentTimeMillis(),
                true
            );
            
            tpaManager.addRequest(request);
            player.sendMessage(plugin.getConfigManager().getLang("tpahere-request-sent").replace("%player%", target.getName()));
            target.sendMessage(plugin.getConfigManager().getLang("tpahere-request-received").replace("%player%", player.getName()));
        }
    }
}
