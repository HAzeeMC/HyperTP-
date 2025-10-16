package com.hazee.hypertp.gui.core;

import com.hazee.hypertp.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class GUI {
    
    protected final Player player;
    protected Inventory inventory;
    protected Map<Integer, Runnable> clickActions;
    
    public GUI(Player player) {
        this.player = player;
        this.clickActions = new HashMap<>();
    }
    
    public abstract void open();
    public abstract void onClick(int slot);
    
    protected void createInventory(String title, int rows) {
        this.inventory = Bukkit.createInventory(player, rows * 9, ChatUtil.colorize(title));
    }
    
    protected void setItem(int slot, ItemStack item, Runnable action) {
        inventory.setItem(slot, item);
        if (action != null) {
            clickActions.put(slot, action);
        }
    }
    
    protected ItemStack createGuiItem(ItemStack baseItem, String name, List<String> lore) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatUtil.colorize(name));
            if (lore != null) {
                meta.setLore(lore.stream().map(ChatUtil::colorize).collect(Collectors.toList()));
            }
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
    public void executeAction(int slot) {
        if (clickActions.containsKey(slot)) {
            clickActions.get(slot).run();
        }
    }
}