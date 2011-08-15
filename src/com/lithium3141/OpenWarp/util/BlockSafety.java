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
        Material last = (new Location(loc.getWorld(), x, y + 1, z)).getBlock().getType();
        Material last2 = (new Location(loc.getWorld(), x, y + 2, z)).getBlock().getType();
        int ny;
        World world = block.getWorld();
        for(ny = y; ny < 127; ny++) {
            Block b = world.getBlockAt(x, ny, z);
            Material m = b.getType();
            if(!m.equals(Material.AIR) && last.equals(Material.AIR) && last2.equals(Material.AIR)) {
                break;
            } else {
                last2 = last;
                last = m;
            }
        }
        
        // If the found block is air, move down
        if(world.getBlockAt(x, ny - 1, z).getType().equals(Material.AIR)) {
            ny = safeNextDownFrom(world.getBlockAt(x, ny, z)).getBlockY();
        }
        
        Location result = new Location(loc.getWorld(), loc.getX(), (double)(ny + 1), loc.getZ());
        result.setPitch(loc.getPitch());
        result.setYaw(loc.getYaw());
        return result;
    }
    
    public static Location safeNextDownFrom(Block block) {
        if(block == null) return null;
        
        Location loc = block.getLocation();
        
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        
        // Find the next lowest block from requested target that has air above it
        Material last = (new Location(loc.getWorld(), x, y + 1, z)).getBlock().getType();
        Material last2 = (new Location(loc.getWorld(), x, y + 2, z)).getBlock().getType();
        int ny;
        World world = block.getWorld();
        for(ny = y; ny > 0; ny--) {
            Block b = world.getBlockAt(x, ny, z);
            Material m = b.getType();
            if(!m.equals(Material.AIR) && last.equals(Material.AIR) && last2.equals(Material.AIR)) {
                break;
            } else {
                last2 = last;
                last = m;
            }
        }
        
        // If the found block doesn't have air, move up
        if(!world.getBlockAt(x, ny + 1, z).getType().equals(Material.AIR)) {
            ny = safeNextUpFrom(world.getBlockAt(x, ny, z)).getBlockY();
        }
        
        Location result = new Location(loc.getWorld(), loc.getX(), (double)(ny + 2), loc.getZ());
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
