package com.lithium3141.OpenWarp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class OWLocationTracker {
    private Map<Player, Location> previousLocations = new HashMap<Player, Location>();
    
    public Location getPreviousLocation(Player player) {
        Location loc = this.previousLocations.get(player); 
        if(loc != null) {
            return loc;
        } else {
            return player.getLocation();
        }
    }
    
    public void setPreviousLocation(Player player, Location location) {
        this.previousLocations.put(player, location);
    }
    
    public void clearPreviousLocation(Player player) {
        this.previousLocations.remove(player);
    }
}
