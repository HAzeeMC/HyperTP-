package com.hazee.hypertp.gui.types;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.gui.core.GUI;
import com.hazee.hypertp.manager.CooldownManager;
import com.hazee.hypertp.util.ChatUtil;
import com.hazee.hypertp.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RTPGUI extends GUI {
    
    private final HyperTP plugin;
    private final CooldownManager cooldownManager;
    
    public RTPGUI(HyperTP plugin, Player player) {
        super(player);
        this.plugin = plugin;
        this.cooldownManager = plugin.getCooldownManager();
    }
    
    @Override
    public void open() {
        String title = plugin.getConfigManager().getLang("gui.rtp.title");
        int rows = plugin.getConfigManager().getConfig().getInt("gui.rtp.rows", 1);
        
        createInventory(title, rows);
        
        // RTP Button
        ItemStack rtpItem = createRTPItem();
        setItem(4, rtpItem, this::performRTP);
        
        // Close button
        ItemStack closeItem = createCloseItem();
        setItem(8, closeItem, () -> player.closeInventory());
        
        player.openInventory(inventory);
    }
    
    @Override
    public void onClick(int slot) {
        executeAction(slot);
    }
    
    private ItemStack createRTPItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatUtil.colorize("&6&lRandom Teleport"));
            
            List<String> lore = new ArrayList<>();
            
            if (cooldownManager.hasCooldown(player.getUniqueId(), "rtp")) {
                long remaining = cooldownManager.getRemainingCooldown(player.getUniqueId(), "rtp");
                lore.add("&cCooldown: " + remaining + "s");
            } else {
                lore.add("&7Click to teleport to a");
                lore.add("&7random safe location.");
                lore.add("");
                lore.add("&aReady to use!");
            }
            
            lore.add("");
            lore.add("&eRange: &f" + getRTPSettings());
            
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
    
    private String getRTPSettings() {
        int min = plugin.getConfigManager().getConfig().getInt("rtp.min-distance", 100);
        int max = plugin.getConfigManager().getConfig().getInt("rtp.max-distance", 1000);
        return min + " - " + max + " blocks";
    }
    
    private void performRTP() {
        player.closeInventory();
        
        if (cooldownManager.hasCooldown(player.getUniqueId(), "rtp")) {
            long remaining = cooldownManager.getRemainingCooldown(player.getUniqueId(), "rtp");
            player.sendMessage(plugin.getConfigManager().getLang("cooldown-rtp").replace("%time%", String.valueOf(remaining)));
            return;
        }
        
        int minDistance = plugin.getConfigManager().getConfig().getInt("rtp.min-distance", 100);
        int maxDistance = plugin.getConfigManager().getConfig().getInt("rtp.max-distance", 1000);
        
        Location safeLocation = LocationUtil.findSafeLocation(player.getWorld(), minDistance, maxDistance);
        
        if (safeLocation == null) {
            player.sendMessage(plugin.getConfigManager().getLang("rtp-failed"));
            return;
        }
        
        player.teleport(safeLocation);
        player.sendMessage(plugin.getConfigManager().getLang("rtp-success"));
        
        int cooldown = plugin.getConfigManager().getConfig().getInt("cooldowns.rtp", 60);
        if (cooldown > 0) {
            cooldownManager.setCooldown(player.getUniqueId(), "rtp", cooldown);
        }
    }
}