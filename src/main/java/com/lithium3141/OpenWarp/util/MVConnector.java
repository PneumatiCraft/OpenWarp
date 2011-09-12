package com.lithium3141.OpenWarp.util;

import org.bukkit.plugin.Plugin;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class MVConnector {
    private MultiverseCore core;

    public MVConnector(Plugin plugin) {
        if(plugin != null) {
            this.core = (MultiverseCore) plugin;
            this.core.getDestinationFactory().registerDestinationType(OpenWarpDestination.class, "ow");
        }
    }
}
