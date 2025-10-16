package com.hazee.hypertp.gui.types;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.gui.core.GUI;
import com.hazee.hypertp.gui.config.GUIConfigLoader;
import com.hazee.hypertp.manager.HomeManager;
import com.hazee.hypertp.model.Home;
import com.hazee.hypertp.task.TeleportCountdownTask;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HomeListGUI extends GUI {
    
    private final HyperTP plugin;
    private final HomeManager homeManager;
    private final GUIConfigLoader guiConfigLoader;
    private final YamlConfiguration config;
    
    public HomeListGUI(HyperTP plugin, Player player) {
        super(player);
        this.plugin = plugin;
        this.homeManager = plugin.getHomeManager();
        this.guiConfigLoader = new GUIConfigLoader(plugin);
        this.config = guiConfigLoader.loadGUIConfig("homegui");
    }
    
    @Override
    public void open() {
        String title = config.getString("gui.home.title", "&6Your Homes");
        int rows = config.getInt("gui.home.rows", 3);
        
        createInventory(title, rows);
        setupBackground();
        setupHomeItems();
        setupControlButtons();
        
        player.openInventory(inventory);
    }
    
    private void setupBackground() {
        List<Integer> backgroundSlots = config.getIntegerList("gui.home.items.background.slots");
        if (!backgroundSlots.isEmpty()) {
            ItemStack backgroundItem = guiConfigLoader.createItemFromConfig(config, "gui.home.items.background", player);
            for (int slot : backgroundSlots) {
                setItem(slot, backgroundItem, null);
            }
        }
    }
    
    private void setupHomeItems() {
        List<Home> homes = homeManager.getPlayerHomes(player.getUniqueId());
        int maxHomesPerPage = config.getInt("home-settings.max-homes-per-page", 14);
        
        if (homes.isEmpty()) {
            // Show empty message
            ItemStack emptyItem = createEmptyHomeItem();
            setItem(13, emptyItem, null);
            return;
        }
        
        int slot = 0;
        for (Home home : homes) {
            if (slot >= maxHomesPerPage) break;
            
            // Find next available slot (skip background slots)
            while (slot < inventory.getSize() && clickActions.containsKey(slot)) {
                slot++;
            }
            
            if (slot < inventory.getSize()) {
                ItemStack homeItem = createHomeItem(home);
                setItem(slot, homeItem, () -> teleportToHome(home));
                slot++;
            }
        }
    }
    
    private void setupControlButtons() {
        // Close button
        List<Integer> closeSlots = config.getIntegerList("gui.home.items.close-button.slots");
        if (!closeSlots.isEmpty()) {
            ItemStack closeItem = guiConfigLoader.createItemFromConfig(config, "gui.home.items.close-button", player);
            for (int slot : closeSlots) {
                setItem(slot, closeItem, () -> player.closeInventory());
            }
        }
    }
    
    private ItemStack createHomeItem(Home home) {
        ItemStack baseItem = guiConfigLoader.createItemFromConfig(config, "gui.home.items.home-item", player);
        ItemMeta meta = baseItem.getItemMeta();
        
        if (meta != null) {
            // Replace home-specific placeholders
            String displayName = meta.getDisplayName().replace("%home_name%", home.getName());
            meta.setDisplayName(displayName);
            
            List<String> lore = meta.getLore();
            if (lore != null) {
                List<String> updatedLore = new ArrayList<>();
                for (String line : lore) {
                    line = line.replace("%home_name%", home.getName())
                              .replace("%home_location%", formatLocation(home.getLocation()))
                              .replace("%home_world%", home.getLocation().getWorld().getName());
                    updatedLore.add(line);
                }
                meta.setLore(updatedLore);
            }
            
            baseItem.setItemMeta(meta);
        }
        
        return baseItem;
    }
    
    private ItemStack createEmptyHomeItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatUtil.colorize("&cNo Homes"));
            List<String> lore = new ArrayList<>();
            lore.add("&7You haven't set any homes yet.");
            lore.add("&7Use &e/sethome <name> &7to create one.");
            meta.setLore(lore.stream().map(com.hazee.hypertp.util.ChatUtil::colorize).collect(java.util.stream.Collectors.toList()));
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    @Override
    public void onClick(int slot) {
        executeAction(slot);
    }
    
    private String formatLocation(org.bukkit.Location location) {
        return String.format("X: %d, Y: %d, Z: %d", 
            (int) location.getX(), 
            (int) location.getY(), 
            (int) location.getZ());
    }
    
    private void teleportToHome(Home home) {
        player.closeInventory();
        
        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "home")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "home");
            player.sendMessage(plugin.getConfigManager().getLang("cooldown-home").replace("%time%", String.valueOf(remaining)));
            return;
        }
        
        int delay = config.getInt("home-settings.teleport-delay", 3);
        
        if (delay > 0) {
            player.sendMessage(plugin.getConfigManager().getLang("teleport-start").replace("%time%", String.valueOf(delay)));
            new TeleportCountdownTask(plugin, player, home.getLocation(), delay, "home").runTaskTimer(plugin, 0L, 20L);
        } else {
            player.teleport(home.getLocation());
            player.sendMessage(plugin.getConfigManager().getLang("teleport-success-home").replace("%home%", home.getName()));
            
            int cooldown = plugin.getConfigManager().getConfig().getInt("cooldowns.home", 0);
            if (cooldown > 0) {
                plugin.getCooldownManager().setCooldown(player.getUniqueId(), "home", cooldown);
            }
        }
    }
}