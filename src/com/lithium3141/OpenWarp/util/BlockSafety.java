package com.lithium3141.OpenWarp.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockSafety {
    public static Location safeNextUpFrom(Block block) {
        if(block == null) return null;
        
        Location loc = block.getLocation();
        
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        
        // Find the next highest block from requested target that has air above it
        Material last = block.getType();
        int ny;
        World world = block.getWorld();
        for(ny = y; ny < 127; ny++) {
            Block b = world.getBlockAt(x, ny, z);
            Material m = b.getType();
            if(m.equals(Material.AIR) && last.equals(Material.AIR)) {
                break;
            } else {
                last = m;
            }
        }
        
        Location result = new Location(loc.getWorld(), loc.getX(), (double)(ny - 1), loc.getZ());
        result.setPitch(loc.getPitch());
        result.setYaw(loc.getYaw());
        return result;
    }
    
    public static Location safeTopFrom(Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        
        // Find the highest block from requested target that is solid with air above it
        Block block = loc.getBlock();
        Material last2 = Material.BEDROCK;
        Material last = Material.BEDROCK;
        int ny;
        World world = block.getWorld();
        for(ny = 126; ny > y; ny--) {
            Block b = world.getBlockAt(x, ny, z);
            Material m = b.getType();
            if(!m.equals(Material.AIR) && last.equals(Material.AIR) && last2.equals(Material.AIR)) {
                break;
            } else {
                last2 = last;
                last = m;
            }
        }
        
        Location result = new Location(loc.getWorld(), loc.getX(), (double)(ny + 1), loc.getZ());
        result.setPitch(loc.getPitch());
        result.setYaw(loc.getYaw());
        return result;
    }
}
