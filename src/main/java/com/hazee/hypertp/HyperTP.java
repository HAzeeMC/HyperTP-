package com.hazee.hypertp;

import com.hazee.hypertp.command.core.HTPCommand;
import com.hazee.hypertp.command.home.*;
import com.hazee.hypertp.command.teleport.*;
import com.hazee.hypertp.gui.core.GUIHandler;
import com.hazee.hypertp.listener.GUIListener;
import com.hazee.hypertp.listener.TeleportListener;
import com.hazee.hypertp.manager.ConfigManager;
import com.hazee.hypertp.manager.CooldownManager;
import com.hazee.hypertp.manager.HomeManager;
import com.hazee.hypertp.manager.TPAManager;
import com.hazee.hypertp.util.FoliaScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class HyperTP extends JavaPlugin {
    
    private static HyperTP instance;
    private ConfigManager configManager;
    private HomeManager homeManager;
    private CooldownManager cooldownManager;
    private TPAManager tpaManager;
    private GUIHandler guiHandler;
    private FoliaScheduler foliaScheduler;
    private boolean isFolia = false;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Detect Folia
        detectFolia();
        this.foliaScheduler = new FoliaScheduler(this);
        
        printASCIIArt();
        
        configManager = new ConfigManager(this);
        homeManager = new HomeManager(this);
        cooldownManager = new CooldownManager(this);
        tpaManager = new TPAManager(this);
        guiHandler = new GUIHandler(this);
        
        configManager.loadConfigs();
        homeManager.loadHomes();
        
        registerCommands();
        registerListeners();
        
        getLogger().info("HyperTP enabled successfully! Author: H_Azee");
        getLogger().info("Scheduler: " + (isFolia ? "Folia Regionized" : "Paper Standard"));
    }
    
    @Override
    public void onDisable() {
        homeManager.saveHomes();
        getLogger().info("HyperTP disabled successfully!");
    }
    
    private void detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
            getLogger().info("╔══════════════════════════════════════╗");
            getLogger().info("║           FOLIA DETECTED!            ║");
            getLogger().info("║    Using Regionized Scheduler       ║");
            getLogger().info("╚══════════════════════════════════════╝");
        } catch (ClassNotFoundException e) {
            isFolia = false;
            getLogger().info("✓ Standard Paper - Using Normal Scheduler");
        }
    }
    
    private void printASCIIArt() {
        getLogger().info("╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        getLogger().info("║                                                                                                                       ║");
        getLogger().info("║   ▄█    █▄         ▄██   ▄           ▄███████▄         ▄████████         ▄████████          ███             ▄███████▄ ║");
        getLogger().info("║  ███    ███        ███   ██▄        ███    ███        ███    ███        ███    ███      ▀█████████▄        ███    ███ ║");
        getLogger().info("║  ███    ███        ███▄▄▄███        ███    ███        ███    █▀         ███    ███         ▀███▀▀██        ███    ███ ║");
        getLogger().info("║ ▄███▄▄▄▄███▄▄      ▀▀▀▀▀▀███        ███    ███       ▄███▄▄▄           ▄███▄▄▄▄██▀          ███   ▀        ███    ███ ║");
        getLogger().info("║▀▀███▀▀▀▀███▀       ▄██   ███      ▀█████████▀       ▀▀███▀▀▀          ▀▀███▀▀▀▀▀            ███          ▀█████████▀  ║");
        getLogger().info("║  ███    ███        ███   ███        ███               ███    █▄       ▀███████████          ███            ███        ║");
        getLogger().info("║  ███    ███        ███   ███        ███               ███    ███        ███    ███          ███            ███        ║");
        getLogger().info("║  ███    █▀          ▀█████▀        ▄████▀             ██████████        ███    ███         ▄████▀         ▄████▀      ║");
        getLogger().info("║                                                                         ███    ███                                    ║");
        getLogger().info("║           Author: H_Azee                                                                                              ║");
        getLogger().info("║        Version: 1.1.0                                                                                                 ║");
        getLogger().info("║        Folia Support: " + (isFolia ? "YES" : "NO") + "                                                                ║");
        getLogger().info("║                                                                                                                       ║");
        getLogger().info("╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
    }
    
    private void registerCommands() {
        getCommand("hypertp").setExecutor(new HTPCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("homelist").setExecutor(new HomeListCommand(this));
        getCommand("tpa").setExecutor(new TPACommand(this));
        getCommand("tpahere").setExecutor(new TPACommand(this));
        getCommand("tpaccept").setExecutor(new TPAcceptCommand(this));
        getCommand("tpdeny").setExecutor(new TPDenyCommand(this));
        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("rtp").setExecutor(new RTPCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }
    
    public static HyperTP getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public HomeManager getHomeManager() {
        return homeManager;
    }
    
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
    
    public TPAManager getTpaManager() {
        return tpaManager;
    }
    
    public GUIHandler getGuiHandler() {
        return guiHandler;
    }
    
    public FoliaScheduler getFoliaScheduler() {
        return foliaScheduler;
    }
    
    public boolean isFolia() {
        return isFolia;
    }
}