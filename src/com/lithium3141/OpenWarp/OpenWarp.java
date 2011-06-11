package com.lithium3141.OpenWarp;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class OpenWarp extends JavaPlugin {
	
	public static final Logger LOG = Logger.getLogger("Minecraft");
	public static final String LOG_PREFIX = "[OpenWarp] ";

	@Override
	public void onDisable() {
		LOG.info(LOG_PREFIX + "Disabled!");
	}

	@Override
	public void onEnable() {
		LOG.info(LOG_PREFIX + "Enabled!");
	}

}
