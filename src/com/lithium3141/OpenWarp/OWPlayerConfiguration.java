package com.lithium3141.OpenWarp;

import java.io.File;

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
	public static final String WARP_CONFIG_FILENAME = "warp.yml";
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
		this.configFolder = new File(this.plugin.getDataFolder(), playerName);
		this.configFolder.mkdirs();
		
		this.generalConfig = new Configuration(new File(this.configFolder, GENERAL_CONFIG_FILENAME));
		this.warpConfig = new Configuration(new File(this.configFolder, WARP_CONFIG_FILENAME));
		this.quotaConfig = new Configuration(new File(this.configFolder, QUOTA_CONFIG_FILENAME));
		
		this.generalConfig.load();
		this.warpConfig.load();
		this.quotaConfig.load();
	}
	
	/**
	 * Save this player configuration to disk.
	 * 
	 * @return true if this player configuration was saved successfully;
	 *         false otherwise.
	 */
	public boolean save() {
		return this.generalConfig.save() && this.warpConfig.save() && this.quotaConfig.save();
	}
}
