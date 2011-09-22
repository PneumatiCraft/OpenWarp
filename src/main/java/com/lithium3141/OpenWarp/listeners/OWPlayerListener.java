package com.lithium3141.OpenWarp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.lithium3141.OpenWarp.OpenWarp;

/**
 * Player listener for OpenWarp. Watches for player join, teleport, and respawn
 * for various player location history updates. Allows use of the <code>/back</code>
 * command.
 */
public class OWPlayerListener extends PlayerListener {
	private OpenWarp plugin;
	
    /**
     * Create a new OWPlayerListener backed by the given OpenWarp instance.
     *
     * @param plugin The OpenWarp instance used for various Bukkit queries.
     */
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
