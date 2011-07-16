package com.lithium3141.OpenWarp;

import org.bukkit.Location;
import org.bukkit.util.config.ConfigurationNode;

public class Warp {
    protected OpenWarp plugin;
	protected String name;
	protected Location location;
	
	public static final String WORLD_KEY = "world";
	public static final String X_KEY = "x";
	public static final String Y_KEY = "y";
	public static final String Z_KEY = "z";
	public static final String PITCH_KEY = "pitch";
	public static final String YAW_KEY = "yaw";
	
	public Warp(OpenWarp plugin, String name, ConfigurationNode node) {
	    this.plugin = plugin;
		this.name = name;
		this.location = this.parseLocation(node);
	}
	
	public Warp(OpenWarp plugin, String name, Location location) {
	    this.plugin = plugin;
	    this.name = name;
	    this.location = location;
	}
	
	private Location parseLocation(ConfigurationNode node) {
	    String worldName = node.getString(WORLD_KEY);
        if(worldName == null) {
            worldName = this.plugin.getServer().getWorlds().get(0).getName();
            OpenWarp.LOG.severe(OpenWarp.LOG_PREFIX + "Malformed warp in configuration: no world for warp " + this.name);
            OpenWarp.LOG.severe(OpenWarp.LOG_PREFIX + "Assuming world " + worldName + " and continuing...");
        }
        
        double x = node.getDouble(X_KEY, 0.0);
        double y = node.getDouble(Y_KEY, 0.0);
        double z = node.getDouble(Z_KEY, 0.0);
        float pitch = (float) node.getDouble(PITCH_KEY, 0.0);
        float yaw = (float) node.getDouble(YAW_KEY, 0.0);
        
        return new Location(this.plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Location getLocation() {
	    return this.location;
	}
	
	public boolean isPublic() {
	    return this.plugin.getPublicWarps().values().contains(this);
	}
	
	public String getDetailString() {
	    return "(" + this.location.getX() + ", " + this.location.getY() + ", " + this.location.getZ() + ") in world " + this.location.getWorld().getName();
	}
}
