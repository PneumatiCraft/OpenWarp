package com.lithium3141.OpenWarp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

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
	
	public static final String TEMP_HOME_NAME = "_HOME";
	public static final String TEMP_BACK_NAME = "_BACK";
	
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
	    // Locate configs
		this.configFolder = new File(this.plugin.getDataFolder(), this.playerName);
		this.configFolder.mkdirs();
		
		// Build config objects from files
		this.generalConfig = new Configuration(new File(this.configFolder, GENERAL_CONFIG_FILENAME));
		this.warpConfig = new Configuration(new File(this.configFolder, WARP_CONFIG_FILENAME));
		this.quotaConfig = new Configuration(new File(this.configFolder, QUOTA_CONFIG_FILENAME));
		
		// Load configs
		this.generalConfig.load();
		this.warpConfig.load();
		this.quotaConfig.load();
		
		// Warps
		if(this.plugin.getPrivateWarps().get(this.playerName) == null) {
		    this.plugin.getPrivateWarps().put(this.playerName, new HashMap<String, Warp>());
		}
		this.plugin.loadWarps(this.warpConfig, this.plugin.getPrivateWarps().get(this.playerName));
		
		// Home
		ConfigurationNode homeNode = this.generalConfig.getNode(OpenWarp.HOME_KEY);
		if(homeNode != null) {
		    this.plugin.getHomes().put(this.playerName, new Warp(this.plugin, TEMP_HOME_NAME, homeNode).getLocation());
		}
		
		// Back
		ConfigurationNode backNode = this.generalConfig.getNode(OpenWarp.BACK_KEY);
		if(backNode != null) {
		    this.plugin.getLocationTracker().setPreviousLocation(this.playerName, new Warp(this.plugin, TEMP_BACK_NAME, backNode).getLocation());
		}
		
		// Quotas
		this.plugin.getQuotaManager().loadPrivateQuotas(this.playerName, this.quotaConfig);
	}
	
	/**
	 * Save this player configuration to disk.
	 * 
	 * @return true if this player configuration was saved successfully
	 *         or skipped; false on error.
	 */
	public boolean save() {
	    // Warps
	    Map<String, Warp> playerWarps = this.plugin.getPrivateWarps(this.playerName);
	    
	    Map<String, Object> configWarps = new HashMap<String, Object>();
	    for(Entry<String, Warp> entry : playerWarps.entrySet()) {
	        configWarps.put(entry.getKey(), entry.getValue().getConfigurationMap());
	    }
	    this.warpConfig.setProperty(OpenWarp.WARPS_LIST_KEY, configWarps);
	    
	    // Home
	    if(this.plugin.getHomes().get(this.playerName) != null) {
	        Map<String, Object> homeWarpConfig = new Warp(this.plugin, TEMP_HOME_NAME, this.plugin.getHomes().get(this.playerName), this.playerName).getConfigurationMap();
	        if(homeWarpConfig != null) {
	            this.generalConfig.setProperty(OpenWarp.HOME_KEY, homeWarpConfig);
	        } else {
	            OpenWarp.LOG.warning(OpenWarp.LOG_PREFIX + "Not writing configuration for player " + this.playerName + " due to missing warp world");
	            return true;
	        }
	    }
	    
	    // Back
	    if(this.plugin.getLocationTracker().getPreviousLocation(this.playerName) != null) {
	        Map<String, Object> backWarpConfig = new Warp(this.plugin, TEMP_BACK_NAME, this.plugin.getLocationTracker().getPreviousLocation(this.playerName), this.playerName).getConfigurationMap();
	        if(backWarpConfig != null) {
	            this.generalConfig.setProperty(OpenWarp.BACK_KEY, backWarpConfig);
	        }
	    }
	    
	    // Quotas
	    this.quotaConfig.setProperty(OpenWarp.QUOTAS_KEY, this.plugin.getQuotaManager().getPlayerQuotaMap(this.playerName));
	    
		return this.generalConfig.save() && this.warpConfig.save() && this.quotaConfig.save();
	}
}
