package com.hazee.hypertp.manager;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.util.FileUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {
    
    private final HyperTP plugin;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;
    private String currentLang;
    private File configFile;
    private File langFile;
    
    public ConfigManager(HyperTP plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfigs() {
        // Create directories
        new File(plugin.getDataFolder(), "langs").mkdirs();
        new File(plugin.getDataFolder(), "gui").mkdirs();
        new File(plugin.getDataFolder(), "data").mkdirs();
        
        // Load main config
        configFile = new File(plugin.getDataFolder(), "config.yml");
        FileUtil.createFileFromResource(plugin, "config.yml", configFile);
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Update config if needed
        updateConfig();
        
        // Load language
        currentLang = config.getString("language", "en");
        loadLanguage();
        
        // Load GUI configs
        loadGUIConfigs();
    }
    
    private void updateConfig() {
        boolean needsSave = false;
        double configVersion = config.getDouble("config-version", 1.0);
        
        // Update to version 1.1
        if (configVersion < 1.1) {
            // Add new cooldown settings
            if (!config.contains("cooldowns.gui")) {
                config.set("cooldowns.gui", 1);
                needsSave = true;
            }
            
            // Add teleport delay settings
            if (!config.contains("teleport-delay.tpa")) {
                config.set("teleport-delay.tpa", 5);
                needsSave = true;
            }
            
            config.set("config-version", 1.1);
            needsSave = true;
        }
        
        if (needsSave) {
            try {
                config.save(configFile);
                plugin.getLogger().info("Config updated to version " + config.getDouble("config-version"));
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to update config: " + e.getMessage());
            }
        }
    }
    
    private void loadLanguage() {
        langFile = new File(plugin.getDataFolder(), "langs/" + currentLang + ".yml");
        FileUtil.createFileFromResource(plugin, "langs/" + currentLang + ".yml", langFile);
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        
        // Update language file if needed
        updateLanguageFile();
    }
    
    private void updateLanguageFile() {
        boolean needsSave = false;
        
        // Add missing messages
        if (!langConfig.contains("cooldown-gui")) {
            langConfig.set("cooldown-gui", "&cYou must wait %time% seconds before opening GUI again.");
            needsSave = true;
        }
        
        if (!langConfig.contains("teleport-countdown")) {
            langConfig.set("teleport-countdown", "&eTeleporting in %time%...");
            needsSave = true;
        }
        
        if (needsSave) {
            try {
                langConfig.save(langFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to update language file: " + e.getMessage());
            }
        }
    }
    
    private void loadGUIConfigs() {
        String[] guiFiles = {"homegui.yml", "tpagui.yml", "rtpgui.yml"};
        
        for (String fileName : guiFiles) {
            File guiFile = new File(plugin.getDataFolder(), "gui/" + fileName);
            FileUtil.createFileFromResource(plugin, "gui/" + fileName, guiFile);
        }
    }
    
    public void reloadConfigs() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadLanguage();
        plugin.getLogger().info("All configurations reloaded successfully.");
    }
    
    public String getLang(String path) {
        String message = langConfig.getString(path);
        if (message == null) {
            plugin.getLogger().warning("Missing language key: " + path);
            return "&cMissing message: " + path;
        }
        return message;
    }
    
    public List<String> getLangList(String path) {
        return langConfig.getStringList(path);
    }
    
    public YamlConfiguration getConfig() {
        return config;
    }
    
    public YamlConfiguration getLangConfig() {
        return langConfig;
    }
    
    public String getCurrentLanguage() {
        return currentLang;
    }
    
    public void setLanguage(String lang) {
        this.currentLang = lang;
        loadLanguage();
    }
}