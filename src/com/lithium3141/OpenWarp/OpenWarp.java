package com.lithium3141.OpenWarp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.lithium3141.OpenWarp.commands.*;
import com.lithium3141.OpenWarp.listeners.OWPlayerListener;
import com.lithium3141.OpenWarp.listeners.OWTeleportListener;
import com.lithium3141.OpenWarp.util.StringUtil;
import com.lithium3141.javastructures.trie.Trie;
import com.lithium3141.javastructures.pair.Range;

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
	public static final String PUBLIC_WARP_CONFIG_FILENAME = "warps.yml";
	
	// Config key names
	public static final String PLAYER_NAMES_LIST_KEY = "players";
	public static final String WARPS_LIST_KEY = "warps";
	
	// Global configuration variables
	public Configuration configuration;
	private Map<String, OWPlayerConfiguration> playerConfigs = new HashMap<String, OWPlayerConfiguration>();
	
	public Configuration publicWarpsConfig;
	private Map<String, Warp> publicWarps = new HashMap<String, Warp>();
	
	// Supported commands
	private Trie<String, Map<Range<Integer>, OWCommand>> commandTrie;
	
	// Per-player data
	private OWLocationTracker locationTracker = new OWLocationTracker();

	@Override
	public void onDisable() {
		if(this.configuration != null) {
			// Save overall configuration
			this.configuration.setProperty(PLAYER_NAMES_LIST_KEY, new ArrayList<String>(this.playerConfigs.keySet()));
			if(!this.configuration.save()) {
				LOG.warning(LOG_PREFIX + "Couldn't save player list; continuing...");
			}
			
			// Save public warps
			this.publicWarpsConfig.setProperty(WARPS_LIST_KEY, this.publicWarps);
			// XXX DEBUGGING
			Map<String, Object> zeroWarp = new HashMap<String, Object>();
			zeroWarp.put("x", 0.0);
			zeroWarp.put("y", 64.0);
			zeroWarp.put("z", 0.0);
			zeroWarp.put("pitch", 0.0f);
			zeroWarp.put("yaw", 0.0f);
			zeroWarp.put("world", "world");
			Map<String, Object> warps = new HashMap<String, Object>();
			warps.put("zero", zeroWarp);
			this.publicWarpsConfig.setProperty(WARPS_LIST_KEY, warps);
			// XXX END DEBUGGING
			if(!this.publicWarpsConfig.save()) {
				LOG.warning(LOG_PREFIX + "Couldn't save public warp list; continuing...");
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
		
		this.publicWarpsConfig = new Configuration(new File(this.getDataFolder(), PUBLIC_WARP_CONFIG_FILENAME));
		this.publicWarpsConfig.load();
		
		// Read player names and create configurations for each
		List<String> playerNames = this.configuration.getStringList(PLAYER_NAMES_LIST_KEY, new ArrayList<String>());
		for(String playerName : playerNames) {
			this.registerPlayerName(playerName);
		}
		
		// Read warp names
		List<String> keys = this.publicWarpsConfig.getKeys(WARPS_LIST_KEY);
		if(keys != null) {
			for(String key : keys) {
				ConfigurationNode node = this.publicWarpsConfig.getNode(WARPS_LIST_KEY + "." + key);
				Warp warp = new Warp(this, key, node);
				this.publicWarps.put(warp.getName(), warp);
			}
		}
		
		// Set up supported commands
		this.loadCommands();
		
		// Start listening for events
		this.loadListeners();
		
		LOG.info(LOG_PREFIX + "Enabled version " + this.getDescription().getVersion());
	}
	
	private void loadCommands() {
		this.commandTrie = new Trie<String, Map<Range<Integer>, OWCommand>>();
		this.registerCommand(new OWWarpCommand(this), 1, 1, "warp");
		
		this.registerCommand(new OWWarpListCommand(this), 0, 0, "warp");
		this.registerCommand(new OWWarpListCommand(this), "warp", "list");
		
		this.registerCommand(new OWWarpDetailCommand(this), "warp", "detail");
		
		this.registerCommand(new OWWarpSetCommand(this), "warp", "set");
		this.registerCommand(new OWWarpSetCommand(this), "setwarp");
		
		this.registerCommand(new OWTopCommand(this), "top");
		
		this.registerCommand(new OWJumpCommand(this), "jump");
		this.registerCommand(new OWJumpCommand(this), "j");
		
		this.registerCommand(new OWBackCommand(this), "back");
	}
	
	private void loadListeners() {
	    OWPlayerListener playerListener = new OWPlayerListener(this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Low, this);
        
        OWTeleportListener teleportListener = new OWTeleportListener(this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, teleportListener, Priority.Normal, this);
	}
	
	/**
	 * Register the given OWCommand with the given key path. Uses the command's
	 * minimumArgs and maximumArgs properties.
	 * 
	 * @see registerCommand(OWCommand, int, int, List<String>)
	 */
	private void registerCommand(OWCommand command, String... keys) {
		this.registerCommand(command, command.getMinimumArgs(), command.getMaximumArgs(), Arrays.asList(keys));
	}
	
	/**
     * Register the given OWCommand with the given key path. Uses the command's
     * minimumArgs and maximumArgs properties.
     * 
     * @see registerCommand(OWCommand, int, int, List<String>)
     */
	private void registerCommand(OWCommand command, List<String> keys) {
	    this.registerCommand(command, command.getMinimumArgs(), command.getMaximumArgs(), keys);
	}
	
	/**
     * Register the given OWCommand with the given key path. Convenience
     * method wrapper.
     * 
     * @see registerCommand(OWCommand, int, int, List<String>)
     */
	private void registerCommand(OWCommand command, int minimumArgs, int maximumArgs, String... keys) {
	    this.registerCommand(command, minimumArgs, maximumArgs, Arrays.asList(keys));
	}
	
	/**
     * Recursively add nodes to the command trie to insert the given
     * OWCommand at the given key path. Overwrites any commands already
     * in the trie at the given key path.
     *  
     * @param command The command to add to the trie
     * @param minimumArgs The smallest number of arguments the command can take
     *                    when reached from the given key path
     * @param minimumArgs The largest number of arguments the command can take
     *                    when reached from the given key path 
     * @param keys The key path to use for the new command
     */
	private void registerCommand(OWCommand command, int minimumArgs, int maximumArgs, List<String> keys) {
	    // Require a non-empty key path
        if(keys.size() == 0) {
            return;
        }
        
        Map<Range<Integer>, OWCommand> commandMap = null;
        try {
            commandMap = this.commandTrie.get(keys);
        } catch(IndexOutOfBoundsException e) {
            this.commandTrie.put(keys, new HashMap<Range<Integer>, OWCommand>());
            commandMap = this.commandTrie.get(keys);
        }
        
        commandMap.put(new Range<Integer>(minimumArgs, maximumArgs), command);
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
		// Construct a trie key path from the command label and args
		List<String> keyPath = new ArrayList<String>();
		keyPath.add(commandLabel.toLowerCase());
		for(int i = 0; i < args.length; i++) {
			keyPath.add(args[i].toLowerCase());
		}
		
		// Locate and run the best matching command from the key path
		List<String> matchPath = this.commandTrie.getDeepestMatch(keyPath);
		Map<Range<Integer>, OWCommand> commandMap = this.commandTrie.get(matchPath);
		List<String> remainingArgs = StringUtil.trimListLeft(keyPath, matchPath);
		
		OWCommand owCommand = null;
		for(Range<Integer> key : commandMap.keySet()) {
		    if(key.contains(remainingArgs.size())) {
		        owCommand = commandMap.get(key);
		        break;
		    }
		}
		if(owCommand != null) {
			return owCommand.execute(sender, command, commandLabel, remainingArgs);
		} else {
			sender.sendMessage(ChatColor.YELLOW + "Command not supported");
			return false;
		}
	}
	
	public Map<String, Warp> getPublicWarps() {
	    return this.publicWarps;
	}
	
	public OWLocationTracker getLocationTracker() {
	    return this.locationTracker;
	}

    /**
     * Get the Warp, if any, matching the given name for the given sender.
     * 
     * @param sender The sender for whom to check for warps
     * @param warpName The name of the warp to find
     * @return In order of precedence: (1) the public warp with the given
     *          name, (2) the private warp belonging to the given sender,
     *          or (3) null.
     */
    public Warp getWarp(CommandSender sender, String warpName) {
        // First check public warps
        for(Entry<String, Warp> entry : this.getPublicWarps().entrySet()) {
            String name = entry.getKey();
            if(name.equalsIgnoreCase(warpName)) {
                return entry.getValue();
            }
        }
        
        // If no match, check private warps
        //TODO
        
        // No match
        return null;
    }

}
