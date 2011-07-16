package com.lithium3141.OpenWarp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

/**
 * Configuration for a single player. Encapsulates all on-disk info
 * for this player, including warps, quotas, etc.
 * 
 * @author lithium3141
 */
public class OWPlayerConfiguration {
	
	// Configuration filenames
	public static final String GENERAL_CONFIG_FILENAME = "general.yml";
	public static final String WARP_CONFIG_FILENAME = "warps.yml";
	public static final String QUOTA_CONFIG_FILENAME = "quota.yml";
	
	// Instance variables
	private OpenWarp plugin;
	private String playerName;
	
	// Active configuration information
	private File configFolder;
	private Configuration generalConfig;
	private Configuration warpConfig;
	private Configuration quotaConfig;
	
	/**
	 * Construct a new player configuration for the given player name.
	 * 
	 * @param plugin The OpenWarp instance handling this player configuration
	 * @param playerName The player to handle configuration for
	 */
	public OWPlayerConfiguration(OpenWarp plugin, String playerName) {
		this.plugin = plugin;
		this.playerName = playerName;
	}
	
	/**
	 * Construct a new player configuration for the given player.
	 * 
	 * @param plugin The OpenWarp instance handling this player configuration
	 * @param player The player to handle configuration for
	 */
	public OWPlayerConfiguration(OpenWarp plugin, Player player) {
		this.plugin = plugin;
		this.playerName = player.getName();
	}
	
	/**
	 * Get the name of the player for which this class handles configuration.
	 * 
	 * @return the player name for this configuration.
	 */
	public String getPlayerName() {
		return this.playerName;
	}
	
	/**
	 * Load this player configuration from disk.
	 */
	public void load() {
		this.configFolder = new File(this.plugin.getDataFolder(), this.playerName);
		this.configFolder.mkdirs();
		
		this.generalConfig = new Configuration(new File(this.configFolder, GENERAL_CONFIG_FILENAME));
		this.warpConfig = new Configuration(new File(this.configFolder, WARP_CONFIG_FILENAME));
		this.quotaConfig = new Configuration(new File(this.configFolder, QUOTA_CONFIG_FILENAME));
		
		this.generalConfig.load();
		this.warpConfig.load();
		this.quotaConfig.load();
		
		if(this.plugin.getPrivateWarps().get(this.playerName) == null) {
		    this.plugin.getPrivateWarps().put(this.playerName, new HashMap<String, Warp>());
		}
		this.plugin.loadWarps(this.warpConfig, this.plugin.getPrivateWarps().get(this.playerName));
	}
	
	/**
	 * Save this player configuration to disk.
	 * 
	 * @return true if this player configuration was saved successfully;
	 *         false otherwise.
	 */
	public boolean save() {
	    Map<String, Warp> playerWarps = this.plugin.getPrivateWarps(this.playerName);
	    
	    Map<String, Object> configWarps = new HashMap<String, Object>();
	    for(Entry<String, Warp> entry : playerWarps.entrySet()) {
	        configWarps.put(entry.getKey(), entry.getValue().getConfigurationMap());
	    }
	    this.warpConfig.setProperty(OpenWarp.WARPS_LIST_KEY, configWarps);
	    
		return this.generalConfig.save() && this.warpConfig.save() && this.quotaConfig.save();
	}
}
