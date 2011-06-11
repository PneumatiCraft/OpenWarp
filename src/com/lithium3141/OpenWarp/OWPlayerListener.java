package com.lithium3141.OpenWarp;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

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
}
