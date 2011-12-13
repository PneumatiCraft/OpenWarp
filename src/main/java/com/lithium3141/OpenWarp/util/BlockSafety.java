package com.lithium3141.OpenWarp.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Utility class for determining "safe" teleport targets. In general, this class
 * exists to provide static methods for performing teleports without injuring
 * a player.
 * <p>
 * A location is declared "safe" if it consists of a non-air block below two
 * air blocks. Note that this is a far cry from Multiverse's definition of "safe."
 */
public class BlockSafety {

    /**
     * Protected constructor to avoid utility class instantiation.
     */
    protected BlockSafety() {
        super();
    }

    /**
     * The height of a typical Minecraft map.
     */
    public static final int WORLD_HEIGHT = 127;

    /**
     * Find the next safe block up from the given block. Searches in the positive
     * Y-direction up to Y=127.
     *
     * @param block The Block at which to start searching.
     * @return The next safe Location up from the given Block, inclusive.
     */
    public static Location safeNextUpFrom(Block block) {
        if(block == null) return null; // SUPPRESS CHECKSTYLE NeedBracesCheck

        Location loc = block.getLocation();

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        // Find the next highest block from requested target that has air above it
        Material last = (new Location(loc.getWorld(), x, y + 1, z)).getBlock().getType();
        Material last2 = (new Location(loc.getWorld(), x, y + 2, z)).getBlock().getType();
        int ny;
        World world = block.getWorld();
        for(ny = y; ny < WORLD_HEIGHT; ny++) {
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

    /**
     * Find the next safe block down from the given block. Searches in the negative
     * Y-direction down to Y=0.
     *
     * @param block The Block at which to start searching.
     * @return The next safe Location down from the given Block, inclusive.
     */
    public static Location safeNextDownFrom(Block block) {
        if(block == null) return null; // SUPPRESS CHECKSTYLE NeedBracesCheck

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

    /**
     * Find the safe block closest to the top of the world from the given Location.
     * Searches in the negative Y-direction starting at Y=127 down to the given location.
     *
     * @param loc The Location at which to stop searching.
     * @return The highest safe Location sharing a column with the given Location.
     */
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
        for(ny = WORLD_HEIGHT - 1; ny > y; ny--) {
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
