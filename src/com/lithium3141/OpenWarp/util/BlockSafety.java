package com.lithium3141.OpenWarp.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockSafety {
    public static Location safeTopFrom(Block block) {
        if(block == null) return null;
        
        Location loc = block.getLocation();
        
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        System.out.println("Aimed at position (" + x + "," + y + "," + z + ")"); // XXX
        
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
        
        Location result = new Location(loc.getWorld(), loc.getX(), (double)(ny - 1), loc.getZ(), loc.getYaw(), loc.getPitch());
        return result;
    }
}
