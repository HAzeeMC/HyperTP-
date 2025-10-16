package com.hazee.hypertp.gui.config;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GUIConfigLoader {
    
    private final HyperTP plugin;
    
    public GUIConfigLoader(HyperTP plugin) {
        this.plugin = plugin;
    }
    
    public YamlConfiguration loadGUIConfig(String guiType) {
        File guiFile = new File(plugin.getDataFolder(), "gui/" + guiType + ".yml");
        if (!guiFile.exists()) {
            plugin.saveResource("gui/" + guiType + ".yml", false);
        }
        return YamlConfiguration.loadConfiguration(guiFile);
    }
    
    public ItemStack createItemFromConfig(YamlConfiguration config, String path, Player player) {
        String materialName = config.getString(path + ".material", "STONE");
        Material material = Material.getMaterial(materialName.toUpperCase());
        if (material == null) {
            material = Material.STONE;
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Set display name
            String name = config.getString(path + ".name", "");
            if (!name.isEmpty()) {
                meta.setDisplayName(ChatUtil.colorize(replacePlaceholders(name, player)));
            }
            
            // Set lore
            List<String> lore = config.getStringList(path + ".lore");
            if (!lore.isEmpty()) {
                List<String> coloredLore = lore.stream()
                    .map(line -> ChatUtil.colorize(replacePlaceholders(line, player)))
                    .collect(Collectors.toList());
                meta.setLore(coloredLore);
            }
            
            // Handle player heads
            if (material == Material.PLAYER_HEAD && meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if (player != null) {
                    skullMeta.setOwningPlayer(player);
                }
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private String replacePlaceholders(String text, Player player) {
        if (text == null) return "";
        
        // Cooldown placeholders
        if (text.contains("%cooldown_status%")) {
            if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "rtp")) {
                long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "rtp");
                text = text.replace("%cooldown_status%", "&cCooldown: " + remaining + "s");
            } else {
                text = text.replace("%cooldown_status%", "&aReady to use!");
            }
        }
        
        // RTP range placeholder
        if (text.contains("%rtp_range%")) {
            int min = plugin.getConfigManager().getConfig().getInt("rtp.min-distance", 100);
            int max = plugin.getConfigManager().getConfig().getInt("rtp.max-distance", 1000);
            text = text.replace("%rtp_range%", min + " - " + max + " blocks");
        }
        
        // RTP cooldown placeholder
        if (text.contains("%rtp_cooldown%")) {
            int cooldown = plugin.getConfigManager().getConfig().getInt("cooldowns.rtp", 60);
            text = text.replace("%rtp_cooldown%", String.valueOf(cooldown));
        }
        
        // Player name placeholder
        if (player != null) {
            text = text.replace("%player_name%", player.getName());
        }
        
        return text;
    }
    
    public List<Integer> getSlotsFromConfig(YamlConfiguration config, String path) {
        List<Integer> slots = new ArrayList<>();
        Object slotsObj = config.get(path + ".slot");
        
        if (slotsObj instanceof Integer) {
            slots.add((Integer) slotsObj);
        } else if (slotsObj instanceof String) {
            String slotsStr = (String) slotsObj;
            if ("auto".equalsIgnoreCase(slotsStr)) {
                // Auto slot placement will be handled by the GUI class
                return new ArrayList<>();
            }
        } else if (slotsObj instanceof List) {
            slots = (List<Integer>) slotsObj;
        }
        
        return slots;
    }
    
    public String getActionFromConfig(YamlConfiguration config, String path) {
        return config.getString(path + ".action", "none");
    }
}