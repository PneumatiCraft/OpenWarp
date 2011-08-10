package com.lithium3141.OpenWarp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.lithium3141.OpenWarp.OpenWarp;

public class OWEntityListener extends EntityListener {
    private OpenWarp plugin;
    
    public OWEntityListener(OpenWarp plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)(event.getEntity());
            System.out.println("Died at: " + player.getLocation());
            this.plugin.getLocationTracker().setPreviousLocation(player, player.getLocation());
        }
    }
}
