package com.lithium3141.OpenWarp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;

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
	}
	
	@Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.plugin.getLocationTracker().setPreviousLocation(event.getPlayer(), event.getFrom());
    }
}
