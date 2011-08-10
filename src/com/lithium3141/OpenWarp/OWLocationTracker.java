package com.lithium3141.OpenWarp;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class OWLocationTracker {
    private Map<Player, Location> previousLocations = new HashMap<Player, Location>();
    private Map<Player, Stack<Location>> locationStacks = new HashMap<Player, Stack<Location>>();
    
    public Location getPreviousLocation(Player player) {
        Location loc = this.previousLocations.get(player); 
        if(loc != null) {
            return loc;
        } else {
            return player.getLocation();
        }
    }
    
    public void setPreviousLocation(Player player, Location location) {
        System.out.println("Setting previous location: " + location);
        this.previousLocations.put(player, location);
    }
    
    public void clearPreviousLocation(Player player) {
        this.previousLocations.remove(player);
    }
    
    public Stack<Location> getLocationStack(Player player) {
        if(this.locationStacks.get(player) == null) {
            this.locationStacks.put(player, new Stack<Location>());
        }
        return this.locationStacks.get(player);
    }
}
