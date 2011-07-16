package com.lithium3141.OpenWarp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class OWLocationTracker {
    private Map<CraftPlayer, Location> previousLocations = new HashMap<CraftPlayer, Location>();
    
    public Location getPreviousLocation(CraftPlayer player) {
        Location loc = this.previousLocations.get(player); 
        if(loc != null) {
            return loc;
        } else {
            return player.getLocation();
        }
    }
    
    public void setPreviousLocation(CraftPlayer player, Location location) {
        this.previousLocations.put(player, location);
    }
    
    public void clearPreviousLocation(CraftPlayer player) {
        this.previousLocations.remove(player);
    }
}
