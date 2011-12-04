package com.lithium3141.OpenWarp.util;

import org.bukkit.plugin.Plugin;

import com.onarandombox.MultiverseCore.MultiverseCore;

/**
 * Utility class for interfacing with Multiverse 2. Required stub
 * for the necessary interfaces.
 */
public class MVConnector {

    /**
     * The instance of the Multiverse-Core plugin to which this object connects.
     */
    private MultiverseCore core;

    /**
     * Create a new MVConnector backed by the given Plugin.
     *
     * @param plugin The Plugin instance for which this connector is operating.
     */
    public MVConnector(Plugin plugin) {
        if(plugin != null) {
            this.core = (MultiverseCore) plugin;
            this.core.getDestFactory().registerDestinationType(OpenWarpDestination.class, "ow");
        }
    }
}
