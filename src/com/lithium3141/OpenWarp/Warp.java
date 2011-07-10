package com.lithium3141.OpenWarp;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.util.config.ConfigurationNode;

public class Warp {
	protected String name;
	protected Location location;
	
	public static final String WORLD_KEY = "world";
	public static final String X_KEY = "x";
	public static final String Y_KEY = "y";
	public static final String Z_KEY = "z";
	public static final String PITCH_KEY = "pitch";
	public static final String YAW_KEY = "yaw";
	
	public Warp(String name, ConfigurationNode node, OpenWarp plugin) {
	    Map<String, Object> contents = node.getAll();
		for(Entry<String, Object> entry : contents.entrySet()) {
		    System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		
		this.name = name;
		this.location = this.parseLocation(node, plugin);
	}
	
	private Location parseLocation(ConfigurationNode node, OpenWarp plugin) {
	    String worldName = node.getString(WORLD_KEY);
        if(worldName == null) {
            worldName = plugin.getServer().getWorlds().get(0).getName();
            OpenWarp.LOG.severe(OpenWarp.LOG_PREFIX + "Malformed warp in configuration: no world for warp " + this.name);
            OpenWarp.LOG.severe(OpenWarp.LOG_PREFIX + "Assuming world " + worldName + " and continuing...");
        }
        
        double x = node.getDouble(X_KEY, 0.0);
        double y = node.getDouble(Y_KEY, 0.0);
        double z = node.getDouble(Z_KEY, 0.0);
        float pitch = (float) node.getDouble(PITCH_KEY, 0.0);
        float yaw = (float) node.getDouble(YAW_KEY, 0.0);
        
        return new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
	}
	
	public String getName() {
		return this.name;
	}
}
