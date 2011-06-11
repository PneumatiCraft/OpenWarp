package com.lithium3141.OpenWarp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * Main plugin class. Responsible for setting up plugin and handling
 * overall configs and player info.
 * 
 * @author lithium3141
 */
public class OpenWarp extends JavaPlugin {
	
	// Logging info
	public static final Logger LOG = Logger.getLogger("Minecraft");
	public static final String LOG_PREFIX = "[OpenWarp] ";
	
	public static final String MASTER_CONFIG_FILENAME = "config.yml";
	
	public static final String PLAYER_NAMES_LIST_KEY = "players";
	
	public Configuration configuration;
	private Map<String, OWPlayerConfiguration> playerConfigs = new HashMap<String, OWPlayerConfiguration>();

	@Override
	public void onDisable() {
		if(this.configuration != null) {
			this.configuration.setProperty(PLAYER_NAMES_LIST_KEY, this.playerConfigs.keySet());
			if(!this.configuration.save()) {
				LOG.warning(LOG_PREFIX + "Couldn't save player list; continuing...");
			}
			
			for(OWPlayerConfiguration config : this.playerConfigs.values()) {
				if(!config.save()) {
					LOG.warning(LOG_PREFIX + " - Couldn't save configuration for player " + config.getPlayerName() + "; continuing...");
				}
			}
		}
		
		LOG.info(LOG_PREFIX + "Disabled!");
	}

	@Override
	public void onEnable() {
		// Set up configuration folder if necessary
		this.getDataFolder().mkdirs();
		
		// Get configuration file (even if nonexistent)
		this.configuration = new Configuration(new File(this.getDataFolder(), MASTER_CONFIG_FILENAME));
		this.configuration.load();
		
		// Read player names and create configurations for each
		List<String> playerNames = this.configuration.getStringList(PLAYER_NAMES_LIST_KEY, new ArrayList<String>());
		for(String playerName : playerNames) {
			this.playerConfigs.put(playerName, new OWPlayerConfiguration(this, playerName));
		}
		
		// Start listening for events
		// TODO continue here
		
		LOG.info(LOG_PREFIX + "Enabled!");
	}

}
