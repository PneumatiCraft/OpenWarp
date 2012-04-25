package com.lithium3141.OpenWarp.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.util.LocationUtil;

/**
 * Player listener for OpenWarp. Watches for player join, teleport, and respawn
 * for various player location history updates. Allows use of the <code>/back</code>
 * command.
 */
public class OWPlayerListener implements Listener {

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

    @EventHandler(priority = EventPriority.LOW)
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        OpenWarp.DEBUG_LOG.fine("Player '" + player.getName() + "'joined.");
        this.plugin.getConfigurationManager().registerPlayerName(player.getName());
    }

    @EventHandler()
    public void playerTeleport(PlayerTeleportEvent event) {
        String fromLocation = LocationUtil.getHumanReadableString(event.getFrom(), 1);
        String toLocation = LocationUtil.getHumanReadableString(event.getTo(), 1);
        OpenWarp.DEBUG_LOG.fine("Player '" + event.getPlayer().getName() + "' teleported ( " + fromLocation + " -> " + toLocation + " ).");
        if (event.isCancelled()) {
            OpenWarp.DEBUG_LOG.fine("...cancelled!");
        }
        if (!locationsWithin(event.getFrom(), event.getTo(), FUZZ_FACTOR)) {
            this.plugin.getLocationTracker().setPreviousLocation(event.getPlayer(), event.getFrom());
        }
    }

    @EventHandler()
    public void playerRespawn(PlayerRespawnEvent event) {
        OpenWarp.DEBUG_LOG.fine("Player '" + event.getPlayer().getName() + "'respawned.");
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
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
