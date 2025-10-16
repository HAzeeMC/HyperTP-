package com.hazee.hypertp.util;

import com.hazee.hypertp.HyperTP;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class FoliaScheduler {
    
    private final HyperTP plugin;
    
    public FoliaScheduler(HyperTP plugin) {
        this.plugin = plugin;
    }
    
    public BukkitTask runTask(Runnable task) {
        if (plugin.isFolia()) {
            // Folia: Global region
            return plugin.getServer().getGlobalRegionScheduler()
                .run(plugin, t -> task.run());
        } else {
            // Paper: Normal scheduler
            return plugin.getServer().getScheduler()
                .runTask(plugin, task);
        }
    }
    
    public BukkitTask runTaskLater(Runnable task, long delay) {
        if (plugin.isFolia()) {
            return plugin.getServer().getGlobalRegionScheduler()
                .runDelayed(plugin, t -> task.run(), delay);
        } else {
            return plugin.getServer().getScheduler()
                .runTaskLater(plugin, task, delay);
        }
    }
    
    public BukkitTask runTaskTimer(Runnable task, long delay, long period) {
        if (plugin.isFolia()) {
            return plugin.getServer().getGlobalRegionScheduler()
                .runAtFixedRate(plugin, t -> task.run(), delay, period);
        } else {
            return plugin.getServer().getScheduler()
                .runTaskTimer(plugin, task, delay, period);
        }
    }
    
    // Entity-specific scheduling for Folia
    public BukkitTask runAtEntity(Entity entity, Runnable task) {
        if (plugin.isFolia()) {
            return entity.getScheduler()
                .run(plugin, t -> task.run(), null);
        } else {
            return plugin.getServer().getScheduler()
                .runTask(plugin, task);
        }
    }
    
    public BukkitTask runAtLocation(Location location, Runnable task) {
        if (plugin.isFolia()) {
            return plugin.getServer().getRegionScheduler()
                .run(plugin, location, t -> task.run());
        } else {
            return plugin.getServer().getScheduler()
                .runTask(plugin, task);
        }
    }
}