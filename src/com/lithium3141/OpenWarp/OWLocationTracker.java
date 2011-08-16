package com.lithium3141.OpenWarp;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class OWLocationTracker {
    private Map<String, Location> previousLocations = new HashMap<String, Location>();
    private Map<String, Stack<Location>> locationStacks = new HashMap<String, Stack<Location>>();
    private Map<String, Integer> ignoreNexts = new HashMap<String, Integer>();
    
    private OpenWarp plugin;
    
    public OWLocationTracker(OpenWarp plugin) {
        this.plugin = plugin;
    }
    
    public Location getPreviousLocation(Player player) {
        return this.getPreviousLocation(player.getName());
    }
    
    public Location getPreviousLocation(String playerName) {
        Location loc = this.previousLocations.get(playerName); 
        if(loc != null) {
            return loc;
        } else {
            return null;
        }
    }
    
    public void setPreviousLocation(Player player, Location location) {
        this.setPreviousLocation(player.getName(), location);
    }
    
    public void setPreviousLocation(String playerName, Location location) {
        if(!(this.ignoreNexts.containsKey(playerName) && this.ignoreNexts.get(playerName) > 0)) {
            System.out.println("Setting previous location (" + playerName + "): " + location);
            this.previousLocations.put(playerName, location);
        }
        if(this.ignoreNexts.containsKey(playerName)) {
            this.ignoreNexts.put(playerName, this.ignoreNexts.get(playerName) - 1);            
        } else {
            this.ignoreNexts.put(playerName, 0);
        }
        this.plugin.saveConfigurations(playerName);
    }
    
    public void clearPreviousLocation(Player player) {
        this.clearPreviousLocation(player.getName());
    }
    
    public void clearPreviousLocation(String playerName) {
        this.previousLocations.remove(playerName);
    }
    
    public void ignoreNextSet(Player player) {
        this.ignoreNextSet(player.getName());
    }
    
    public void ignoreNextSet(String playerName) {
        if(this.ignoreNexts.containsKey(playerName)) {
            this.ignoreNexts.put(playerName, this.ignoreNexts.get(playerName) - 1);
        } else {
            this.ignoreNexts.put(playerName, 1);
        }
    }
    
    public void ignoreNextSets(Player player, int count) {
        this.ignoreNextSets(player.getName(), count);
    }
    
    public void ignoreNextSets(String playerName, int count) {
        this.ignoreNexts.put(playerName, count);
    }
    
    public Stack<Location> getLocationStack(Player player) {
        return this.getLocationStack(player.getName());
    }
    
    public Stack<Location> getLocationStack(String playerName) {
        if(this.locationStacks.get(playerName) == null) {
            this.locationStacks.put(playerName, new Stack<Location>());
        }
        return this.locationStacks.get(playerName);
    }
}
