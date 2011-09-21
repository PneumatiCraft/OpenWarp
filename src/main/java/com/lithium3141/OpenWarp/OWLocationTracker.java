package com.lithium3141.OpenWarp;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Utility class responsible for tracking player location changes. Used in part to
 * implement the <code>/back</code> and various warp stack functions.
 * <p>
 * For the purposes of the tracker, the phrase "previous location" has special meaning:
 * it is the last location of a given Player before a teleport or death event. Note
 * that OpenWarp need not instantiate the teleport for it to be recorded; for example,
 * a Multiverse-performed <code>/mvtp</code> command will trigger OpenWarp to record
 * the player's previous location.
 * <p>
 * In addition to a previous location, each Player also has a location stack, which is
 * managed explicitly using the <code>/warp stack</code> set of commands. This stack
 * provides a clearer chronological history that can be managed by the player directly,
 * rather than implicitly by teleportation or death events.
 */
public class OWLocationTracker {
    private Map<String, Location> previousLocations = new HashMap<String, Location>();
    private Map<String, Stack<Location>> locationStacks = new HashMap<String, Stack<Location>>();
    private Map<String, Integer> ignoreNexts = new HashMap<String, Integer>();
    
    private OpenWarp plugin;
    
    /**
     * Create a new location tracker for the given OpenWarp instance.
     *
     * @param plugin The OpenWarp instance for which to track player locations.
     */
    public OWLocationTracker(OpenWarp plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Get the last recorded location of the given Player. This location is updated
     * whenever the Player teleports or dies. Calls #getPreviousLocation(String)
     * internally, as the location map is based on player name strings.
     *
     * @param player The Player for whom to get the last known location.
     * @return The Location of the given Player immediately before their most recent
     * teleport or death, or <code>null</code> if no such location is known.
     * @see #getPreviousLocation(String)
     */
    public Location getPreviousLocation(Player player) {
        return this.getPreviousLocation(player.getName());
    }
    
    /**
     * Get the last recorded location of the player with the given name. This location
     * is updated whenever the player teleports or dies.
     *
     * @param playerName The name of the player for whom to get the last known location.
     * @return The Location of the given player immediately before their most recent
     * teleport or death, or <code>null</code> if no such location is known.
     */
    public Location getPreviousLocation(String playerName) {
        Location loc = this.previousLocations.get(playerName); 
        if(loc != null) {
            return loc;
        } else {
            return null;
        }
    }
    
    /**
     * Set the last recorded location of the given Player. Calls
     * #setPreviousLocation(String, Location) internally.
     *
     * @param player The Player for whom to update the location.
     * @param location The Location to record as "previous location."
     */
    public void setPreviousLocation(Player player, Location location) {
        this.setPreviousLocation(player.getName(), location);
    }
    
    /**
     * Set the last recorded location of the given player.
     *
     * @param playerName The player for whom to update the location.
     * @param location The Location to record as "previous location."
     */
    public void setPreviousLocation(String playerName, Location location) {
        if(!(this.ignoreNexts.containsKey(playerName) && this.ignoreNexts.get(playerName) > 0)) {
            OpenWarp.LOG.fine(OpenWarp.LOG_PREFIX + "Setting previous location (" + playerName + "): " + location);
            this.previousLocations.put(playerName, location);
        }
        if(this.ignoreNexts.containsKey(playerName)) {
            this.ignoreNexts.put(playerName, this.ignoreNexts.get(playerName) - 1);            
        } else {
            this.ignoreNexts.put(playerName, 0);
        }
        this.plugin.saveConfigurations(playerName);
    }
    
    /**
     * Remove the last known location for the given Player. Calls
     * #clearPreviousLocation(String) internally.
     *
     * @param player The Player for whom to clear the last known location.
     */
    public void clearPreviousLocation(Player player) {
        this.clearPreviousLocation(player.getName());
    }
    
    /**
     * Remove the last known location for the given player.
     *
     * @param playerName The player for whom to clear the last known location.
     */
    public void clearPreviousLocation(String playerName) {
        this.previousLocations.remove(playerName);
    }
    
    /**
     * Ignore the next call to #setPreviousLocation(String, Location) for the
     * given Player. Calls #ignoreNextSet(String) internally.
     * <p>
     * This method is most often used when handling repeated or multiply-fired
     * events from other plugins. It gives OpenWarp the ability to ignore spurious
     * events that would otherwise set the previous location falsely.
     *
     * @param player The Player for whom to ignore the next previous location setting.
     */
    public void ignoreNextSet(Player player) {
        this.ignoreNextSet(player.getName());
    }
    
    /**
     * Ignore the next call to #setPreviousLocation(String, Location) for the
     * given Player.
     * <p>
     * This method is most often used when handling repeated or multiply-fired
     * events from other plugins. It gives OpenWarp the ability to ignore spurious
     * events that would otherwise set the previous location falsely.
     *
     * @param playerName The player for whom to ignore the next previous location setting.
     */
    public void ignoreNextSet(String playerName) {
        if(this.ignoreNexts.containsKey(playerName)) {
            this.ignoreNexts.put(playerName, this.ignoreNexts.get(playerName) - 1);
        } else {
            this.ignoreNexts.put(playerName, 1);
        }
    }
    
    /**
     * Ignore the next calls to #setPreviousLocation(String, Location) for the
     * given Player. Calls #ignoreNextSets(String, int) internally.
     * <p>
     * This method is most often used when handling repeated or multiply-fired
     * events from other plugins. It gives OpenWarp the ability to ignore spurious
     * events that would otherwise set the previous location falsely.
     *
     * @param player The Player for whom to ignore the next previous location setting.
     * @param count The number of sets to ignore.
     */
    public void ignoreNextSets(Player player, int count) {
        this.ignoreNextSets(player.getName(), count);
    }
    
    /**
     * Ignore the next calls to #setPreviousLocation(String, Location) for the
     * given Player.
     * <p>
     * This method is most often used when handling repeated or multiply-fired
     * events from other plugins. It gives OpenWarp the ability to ignore spurious
     * events that would otherwise set the previous location falsely.
     *
     * @param playerName The player for whom to ignore the next previous location setting.
     * @param count The number of sets to ignore.
     */
    public void ignoreNextSets(String playerName, int count) {
        this.ignoreNexts.put(playerName, count);
    }
    
    /**
     * Get the location stack for the given Player. Calls #getLocationStack(String)
     * internally.
     *
     * @param player The Player for whom to fetch the location stack.
     * @return A stack of Location instances set by the Player onto their stack.
     */
    public Stack<Location> getLocationStack(Player player) {
        return this.getLocationStack(player.getName());
    }
    
    /**
     * Get the location stack for the given Player.
     *
     * @param playerName The player for whom to fetch the location stack.
     * @return A stack of Location instances set by the player onto their stack.
     */
    public Stack<Location> getLocationStack(String playerName) {
        if(this.locationStacks.get(playerName) == null) {
            this.locationStacks.put(playerName, new Stack<Location>());
        }
        return this.locationStacks.get(playerName);
    }
}
