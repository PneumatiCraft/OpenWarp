package com.lithium3141.OpenWarp.listeners;

import org.bukkit.Location;
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

    /**
     * The distance a player must travel to update their previous location.
     */
    public static final double FUZZ_FACTOR = 1.0;

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        OpenWarp.DEBUG_LOG.fine("Player '" + player.getName() + "'joined.");
        this.plugin.getConfigurationManager().registerPlayerName(player.getName());
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        OpenWarp.DEBUG_LOG.fine("Player '" + event.getPlayer().getName() + "' teleported ( " + prettyLocation(event.getFrom()) + " -> " + prettyLocation(event.getTo()) + " ).");
        if(event.isCancelled()) {
            OpenWarp.DEBUG_LOG.fine("...cancelled!");
        }
        if(!locationsWithin(event.getFrom(), event.getTo(), FUZZ_FACTOR)) {
            this.plugin.getLocationTracker().setPreviousLocation(event.getPlayer(), event.getFrom());
        }
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        OpenWarp.DEBUG_LOG.fine("Player '" + event.getPlayer().getName() + "'respawned.");
    }

    private String prettyLocation(Location loc) {
        return loc.getWorld().getName() + "(" + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ")";
    }

    private boolean locationsWithin(Location l1, Location l2, double fuzz) {
        try {
            return l1.distance(l2) < fuzz;
        } catch(IllegalArgumentException e) {
            return false;
        }
    }
}
