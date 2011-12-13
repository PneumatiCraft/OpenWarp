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

    /**
     * The OpenWarp instance backing this player listener.
     */
    private OpenWarp plugin;

    /**
     * Create a new OWPlayerListener backed by the given OpenWarp instance.
     *
     * @param ow The OpenWarp instance used for various Bukkit queries.
     */
    public OWPlayerListener(OpenWarp ow) {
        this.plugin = ow;
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
        if (event.isCancelled()) {
            OpenWarp.DEBUG_LOG.fine("...cancelled!");
        }
        if (!locationsWithin(event.getFrom(), event.getTo(), FUZZ_FACTOR)) {
            this.plugin.getLocationTracker().setPreviousLocation(event.getPlayer(), event.getFrom());
        }
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        OpenWarp.DEBUG_LOG.fine("Player '" + event.getPlayer().getName() + "'respawned.");
    }

    /**
     * Format a Location instance into a human-readable String.
     *
     * @param loc The Location to format.
     * @return A String containing a readable representation of the given Location.
     */
    private String prettyLocation(Location loc) {
        return loc.getWorld().getName() + "(" + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ")";
    }

    /**
     * Check whether two locations are within a given distance of one another.
     *
     * @param l1 The first Location.
     * @param l2 The second Location.
     * @param fuzz The maximum distance between the two locations.
     * @return True if the distance between l1 and l2 is less than fuzz; false otherwise.
     */
    private boolean locationsWithin(Location l1, Location l2, double fuzz) {
        try {
            return l1.distance(l2) < fuzz;
        } catch(IllegalArgumentException e) {
            return false;
        }
    }
}
