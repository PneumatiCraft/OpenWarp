package com.lithium3141.OpenWarp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class OpenWarp extends JavaPlugin {
	
	public static final Logger LOG = Logger.getLogger("Minecraft");
	public static final String LOG_PREFIX = "[OpenWarp] ";
	
	public static final String MASTER_CONFIG_FILENAME = "config.yml";
	
	public static final String PLAYER_NAMES_LIST_KEY = "players";
	
	public Configuration configuration;
	public List<String> playerNames = new ArrayList<String>();

	@Override
	public void onDisable() {
		if(this.configuration != null) {
			this.configuration.setProperty(PLAYER_NAMES_LIST_KEY, this.playerNames);
			
			if(!this.configuration.save()) {
				LOG.warning(LOG_PREFIX + "Couldn't save player configuration; continuing...");
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
		this.playerNames = this.configuration.getStringList(PLAYER_NAMES_LIST_KEY, new ArrayList<String>());
		
		LOG.info(LOG_PREFIX + "Enabled!");
	}

}
