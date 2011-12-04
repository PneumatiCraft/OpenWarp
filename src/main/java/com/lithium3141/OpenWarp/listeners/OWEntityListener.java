package com.lithium3141.OpenWarp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.lithium3141.OpenWarp.OpenWarp;

/**
 * Entity listener for OpenWarp. Implements the <code>onEntityDeath</code>
 * method in order to update player location histories; in turn, this allows
 * OpenWarp to support players returning to death points with the <code>/back</code>
 * command.
 */
public class OWEntityListener extends EntityListener {

    /**
     * The OpenWarp instance backing this entity listener.
     */
    private OpenWarp plugin;

    /**
     * Create a new OWEntityListener backed by the given OpenWarp instance.
     *
     * @param plugin The OpenWarp instance used for various Bukkit queries.
     */
    public OWEntityListener(OpenWarp plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            OpenWarp.DEBUG_LOG.fine("Player died.");
            Player player = (Player)(event.getEntity());
            this.plugin.getLocationTracker().setPreviousLocation(player, player.getLocation());
        }
    }
}
