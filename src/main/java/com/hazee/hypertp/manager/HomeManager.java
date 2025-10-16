package com.hazee.hypertp.manager;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.model.Home;
import com.hazee.hypertp.util.FileUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {
    
    private final HyperTP plugin;
    private final Map<UUID, Map<String, Home>> playerHomes;
    private File homesFile;
    private YamlConfiguration homesConfig;
    
    public HomeManager(HyperTP plugin) {
        this.plugin = plugin;
        this.playerHomes = new ConcurrentHashMap<>();
    }
    
    public void loadHomes() {
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        FileUtil.createFileIfNotExists(homesFile);
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        
        playerHomes.clear();
        
        for (String playerUUID : homesConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(playerUUID);
            Map<String, Home> homes = new HashMap<>();
            
            for (String homeName : homesConfig.getConfigurationSection(playerUUID).getKeys(false)) {
                String path = playerUUID + "." + homeName;
                Location location = homesConfig.getLocation(path + ".location");
                
                if (location != null) {
                    homes.put(homeName.toLowerCase(), new Home(uuid, homeName, location));
                }
            }
            
            playerHomes.put(uuid, homes);
        }
    }
    
    public void saveHomes() {
        if (homesConfig == null) return;
        
        for (String key : homesConfig.getKeys(false)) {
            homesConfig.set(key, null);
        }
        
        for (Map.Entry<UUID, Map<String, Home>> entry : playerHomes.entrySet()) {
            String uuid = entry.getKey().toString();
            for (Home home : entry.getValue().values()) {
                String path = uuid + "." + home.getName();
                homesConfig.set(path + ".location", home.getLocation());
            }
        }
        
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save homes.yml: " + e.getMessage());
        }
    }
    
    public boolean hasHome(UUID playerUUID, String homeName) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        return homes != null && homes.containsKey(homeName.toLowerCase());
    }
    
    public Home getHome(UUID playerUUID, String homeName) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        return homes != null ? homes.get(homeName.toLowerCase()) : null;
    }
    
    public void setHome(UUID playerUUID, Home home) {
        Map<String, Home> homes = playerHomes.computeIfAbsent(playerUUID, k -> new HashMap<>());
        homes.put(home.getName().toLowerCase(), home);
    }
    
    public void deleteHome(UUID playerUUID, String homeName) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        if (homes != null) {
            homes.remove(homeName.toLowerCase());
        }
    }
    
    public List<Home> getPlayerHomes(UUID playerUUID) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        return homes != null ? new ArrayList<>(homes.values()) : new ArrayList<>();
    }
    
    public int getHomeCount(UUID playerUUID) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        return homes != null ? homes.size() : 0;
    }
}