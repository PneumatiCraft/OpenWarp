package com.lithium3141.OpenWarp.listeners;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.lithium3141.OpenWarp.util.MVConnector;

public class OWPluginListener extends ServerListener {
    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin p = event.getPlugin();
        if(p.getDescription().getName().equalsIgnoreCase("Multiverse-Core")) {
            new MVConnector(p);
            System.out.println("[OpenWarp] Found Multiverse 2, Support Enabled.");
        }
    }
}
