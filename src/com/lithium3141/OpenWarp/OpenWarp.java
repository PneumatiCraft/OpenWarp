package com.lithium3141.OpenWarp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
	public static final String GLOBAL_WARP_CONFIG_FILENAME = "warps.yml";
	
	// Config key names
	public static final String PLAYER_NAMES_LIST_KEY = "players";
	public static final String WARPS_LIST_KEY = "warps";
	
	// Global configuration variables
	public Configuration configuration;
	private Map<String, OWPlayerConfiguration> playerConfigs = new HashMap<String, OWPlayerConfiguration>();
	
	public Configuration globalWarpsConfig;
	private List<String> globalWarpNames;
	
	// Supported commands
	private OWCommandTrie commandTrie;

	@Override
	public void onDisable() {
		if(this.configuration != null) {
			// Save overall configuration
			this.configuration.setProperty(PLAYER_NAMES_LIST_KEY, new ArrayList<String>(this.playerConfigs.keySet()));
			if(!this.configuration.save()) {
				LOG.warning(LOG_PREFIX + "Couldn't save player list; continuing...");
			}
			
			// Save global warps
			this.globalWarpsConfig.setProperty(WARPS_LIST_KEY, this.globalWarpNames);
			if(!this.globalWarpsConfig.save()) {
				LOG.warning(LOG_PREFIX + "Couldn't save global warp list; continuing...");
			}
			
			// Save player-specific data
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
		
		this.globalWarpsConfig = new Configuration(new File(this.getDataFolder(), GLOBAL_WARP_CONFIG_FILENAME));
		this.globalWarpsConfig.load();
		
		// Read player names and create configurations for each
		List<String> playerNames = this.configuration.getStringList(PLAYER_NAMES_LIST_KEY, new ArrayList<String>());
		for(String playerName : playerNames) {
			this.registerPlayerName(playerName);
		}
		
		// Read warp names
		this.globalWarpNames = this.globalWarpsConfig.getStringList(WARPS_LIST_KEY, new ArrayList<String>());
		
		// Set up supported commands
		this.commandTrie = new OWCommandTrie();
		
		// Start listening for events
		OWPlayerListener playerListener = new OWPlayerListener(this);
		this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Low, this);
		
		LOG.info(LOG_PREFIX + "Enabled!");
	}
	
	/**
	 * Register a player with the OpenWarp plugin. Create a new
	 * OWPlayerConfiguration instance for the given Player if no such 
	 * configuration exists yet.
	 * 
	 * @param playerName The player to register
	 * @see OWPlayerConfiguration
	 */
	public void registerPlayerName(String playerName) {
		if(this.playerConfigs.get(playerName) == null) {
			OWPlayerConfiguration playerConfig = new OWPlayerConfiguration(this, playerName);
			playerConfig.load();
			this.playerConfigs.put(playerName, playerConfig);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String[] keyPath = new String[args.length + 1];
		keyPath[0] = commandLabel.toLowerCase();
		for(int i = 0; i < args.length; i++) {
			keyPath[i + 1] = args[i].toLowerCase();
		}
		
		OWCommand owCommand = this.commandTrie.getDeepestMatch(keyPath);
		if(owCommand != null) {
			return true;
		} else {
			sender.sendMessage(ChatColor.YELLOW + "Command not supported");
			return false;
		}
	}

}
