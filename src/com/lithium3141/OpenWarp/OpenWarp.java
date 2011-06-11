package com.lithium3141.OpenWarp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
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
	
	// Global config filenames
	public static final String MASTER_CONFIG_FILENAME = "config.yml";
	
	// Config key names
	public static final String PLAYER_NAMES_LIST_KEY = "players";
	
	// Global configuration variables
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
		OWPlayerListener playerListener = new OWPlayerListener(this);
		this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Low, this);
		
		LOG.info(LOG_PREFIX + "Enabled!");
	}
	
	public void registerPlayer(Player player) {
		String playerName = player.getName();
		if(this.playerConfigs.get(playerName) == null) {
			LOG.info(LOG_PREFIX + "No configuration for player " + playerName + "; creating...");
			this.playerConfigs.put(playerName, new OWPlayerConfiguration(this, playerName));
		}
	}

}
