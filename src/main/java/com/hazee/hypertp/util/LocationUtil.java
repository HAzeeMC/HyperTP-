package com.hazee.hypertp.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Random;

public class LocationUtil {
    
    private static final Random random = new Random();
    private static final Material[] UNSAFE_BLOCKS = {
        Material.LAVA, Material.FIRE, Material.CAMPFIRE, Material.SOUL_FIRE,
        Material.SOUL_CAMPFIRE, Material.MAGMA_BLOCK, Material.CACTUS
    };
    
    public static Location findSafeLocation(World world, int minDistance, int maxDistance) {
        for (int attempt = 0; attempt < 50; attempt++) {
            int x = random.nextInt(maxDistance - minDistance) + minDistance;
            int z = random.nextInt(maxDistance - minDistance) + minDistance;
            
            // Randomize positive/negative coordinates
            if (random.nextBoolean()) x = -x;
            if (random.nextBoolean()) z = -z;
            
            Location potentialLocation = new Location(world, x, world.getMaxHeight(), z);
            Location safeLocation = findSafeY(potentialLocation);
            
            if (safeLocation != null && isLocationSafe(safeLocation)) {
                return safeLocation.add(0.5, 0, 0.5); // Center in block
            }
        }
        return null;
    }
    
    private static Location findSafeY(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        
        // Start from highest Y and go down
        for (int y = world.getMaxHeight() - 1; y > world.getMinHeight() + 1; y--) {
            Block feetBlock = world.getBlockAt(x, y, z);
            Block headBlock = world.getBlockAt(x, y + 1, z);
            Block groundBlock = world.getBlockAt(x, y - 1, z);
            
            // Check if this is a valid standing position
            if (isSolidGround(groundBlock) && feetBlock.getType().isAir() && headBlock.getType().isAir()) {
                return new Location(world, x, y, z);
            }
        }
        
        return null;
    }
    
    private static boolean isSolidGround(Block block) {
        Material type = block.getType();
        return type.isSolid() && 
               !type.name().contains("LEAVES") &&
               !type.name().contains("SAPLING") &&
               type != Material.ICE &&
               type != Material.FROSTED_ICE &&
               type != Material.PACKED_ICE &&
               type != Material.BLUE_ICE;
    }
    
    private static boolean isLocationSafe(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        // Check 3x3 area around the location
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block checkBlock = world.getBlockAt(x + dx, y, z + dz);
                Block belowBlock = world.getBlockAt(x + dx, y - 1, z + dz);
                Block aboveBlock = world.getBlockAt(x + dx, y + 1, z + dz);
                
                // Check for unsafe blocks
                for (Material unsafe : UNSAFE_BLOCKS) {
                    if (checkBlock.getType() == unsafe || belowBlock.getType() == unsafe || aboveBlock.getType() == unsafe) {
                        return false;
                    }
                }
                
                // Check for void
                if (belowBlock.getType() == Material.AIR) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public static boolean isInSafezone(Location location) {
        // Implement safezone logic here
        // This could check world guard regions or config-defined areas
        return false;
    }
    
    public static double calculateDistance2D(Location loc1, Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }
    
    public static double calculateDistance3D(Location loc1, Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dy = loc1.getY() - loc2.getY();
        double dz = loc1.getZ() - loc2.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}