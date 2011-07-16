package com.lithium3141.OpenWarp.listeners;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.lithium3141.OpenWarp.OpenWarp;

public class OWTeleportListener extends PlayerListener {
    private OpenWarp plugin;
    
    public OWTeleportListener(OpenWarp plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.plugin.getLocationTracker().setPreviousLocation(event.getPlayer(), event.getFrom());
    }
}
