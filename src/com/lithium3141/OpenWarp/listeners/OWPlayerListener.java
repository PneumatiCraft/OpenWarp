package com.lithium3141.OpenWarp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.lithium3141.OpenWarp.OpenWarp;

public class OWPlayerListener extends PlayerListener {
	private OpenWarp plugin;
	
	public OWPlayerListener(OpenWarp plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		this.plugin.registerPlayerName(player.getName());
		this.plugin.getLocationTracker().ignoreNextSets(player, 2);
	}
	
	@Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.plugin.getLocationTracker().setPreviousLocation(event.getPlayer(), event.getFrom());
        if(!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            this.plugin.getLocationTracker().ignoreNextSet(event.getPlayer());
        }
    }
	
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
	    this.plugin.getLocationTracker().ignoreNextSet(event.getPlayer());
	}
}
