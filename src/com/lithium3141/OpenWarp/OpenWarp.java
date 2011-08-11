package com.lithium3141.OpenWarp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.lithium3141.OpenWarp.commands.*;
import com.lithium3141.OpenWarp.listeners.OWEntityListener;
import com.lithium3141.OpenWarp.listeners.OWPlayerListener;
import com.pneumaticraft.commandhandler.CommandHandler;

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
	public static final String QUOTAS_KEY = "quotas";
    public static final String QUOTA_PUBLIC_KEY = "public";
    public static final String QUOTA_PRIVATE_KEY = "private";
    public static final String HOME_KEY = "home";
	
	// Global configuration variables
	public Configuration configuration;
	private Map<String, OWPlayerConfiguration> playerConfigs = new HashMap<String, OWPlayerConfiguration>(); // player name => config
	
	public Configuration publicWarpsConfig;
	private Map<String, Warp> publicWarps = new HashMap<String, Warp>(); // warp name => warp
	private Map<String, Map<String, Warp>> privateWarps = new HashMap<String, Map<String, Warp>>(); // player name => (warp name => warp)
	private Map<String, Location> homes = new HashMap<String, Location>(); // player name => home
	
	private OWQuotaManager quotaManager;
	
	private OWPermissionsHandler permissionsHandler;

    // Supported commands
	private CommandHandler commandHandler;
	
	// Per-player data
	private OWLocationTracker locationTracker = new OWLocationTracker();

	@Override
	public void onDisable() {
		this.saveAllConfigurations();
		
		LOG.info(LOG_PREFIX + "Disabled!");
	}
	
	public void saveAllConfigurations() {
	    if(this.configuration != null) {
            // Save overall configuration
            this.configuration.setProperty(PLAYER_NAMES_LIST_KEY, new ArrayList<String>(this.playerConfigs.keySet()));
            if(!this.configuration.save()) {
                LOG.warning(LOG_PREFIX + "Couldn't save player list; continuing...");
            }
            
            // Save public warps
            Map<String, Object> warps = new HashMap<String, Object>();
            for(Entry<String, Warp> entry : this.publicWarps.entrySet()) {
                warps.put(entry.getKey(), entry.getValue().getConfigurationMap());
            }
            
            this.publicWarpsConfig.setProperty(WARPS_LIST_KEY, warps);
            if(!this.publicWarpsConfig.save()) {
                LOG.warning(LOG_PREFIX + "Couldn't save public warp list; continuing...");
            }
            
            // Save global quotas
            this.configuration.setProperty(QUOTAS_KEY, this.quotaManager.getGlobalQuotaMap());
            
            // Save player-specific data
            for(OWPlayerConfiguration config : this.playerConfigs.values()) {
                if(!config.save()) {
                    LOG.warning(LOG_PREFIX + " - Couldn't save configuration for player " + config.getPlayerName() + "; continuing...");
                }
            }
        }
	}

	@Override
	public void onEnable() {
		// Set up configuration folder if necessary
		this.getDataFolder().mkdirs();
		
		// Create overall permission
		this.getServer().getPluginManager().addPermission(new Permission("openwarp.*", PermissionDefault.OP));
		
		// Get configuration file (even if nonexistent)
		this.configuration = new Configuration(new File(this.getDataFolder(), MASTER_CONFIG_FILENAME));
		this.configuration.load();
		
		this.publicWarpsConfig = new Configuration(new File(this.getDataFolder(), PUBLIC_WARP_CONFIG_FILENAME));
		this.publicWarpsConfig.load();
		
		// Instantiate quota manager, permissions - need them for player configs
        this.quotaManager = new OWQuotaManager(this);
        this.permissionsHandler = new OWPermissionsHandler(this);
		
		// Read player names and create configurations for each
		List<String> playerNames = this.configuration.getStringList(PLAYER_NAMES_LIST_KEY, new ArrayList<String>());
		for(String playerName : playerNames) {
			this.registerPlayerName(playerName);
		}
		
		// Read warp names
		this.loadWarps(this.publicWarpsConfig, this.publicWarps);
		
		// Read quotas
		this.quotaManager.loadGlobalQuotas(this.configuration);
		
		// Set up supported commands
		this.loadCommands();
		
		// Instantiate permission nodes for all relevant objects
		this.loadWarpPermissions();
		this.loadHomePermissions();
		
		// Start listening for events
		this.loadListeners();
		
		LOG.info(LOG_PREFIX + "Enabled version " + this.getDescription().getVersion());
	}
	
	/**
	 * Load warps listed at the given ConfigurationNode into the given warps
	 * map. Mutates the `target` argument.
	 * 
	 * @param config The ConfigurationNode to search for warps. Must have the
	 *               `warps` key.
	 * @param target The map into which to load new Warp objects.
	 */
	public void loadWarps(ConfigurationNode config, Map<String, Warp> target) {
	    List<String> keys = config.getKeys(WARPS_LIST_KEY);
        if(keys != null) {
            for(String key : keys) {
                ConfigurationNode node = config.getNode(WARPS_LIST_KEY + "." + key);
                Warp warp = new Warp(this, key, node);
                target.put(warp.getName(), warp);
            }
        }
	}
	
	/**
	 * Create warp permission nodes for all loaded warps.
	 */
	public void loadWarpPermissions() {
	    PluginManager pm = this.getServer().getPluginManager();
	    
	    // Finagle a new permission for public warps
	    Map<String, Boolean> publicWarpChildren = new HashMap<String, Boolean>();
	    for(Warp publicWarp : this.getPublicWarps().values()) {
	        String permString = "openwarp.warp.access.public." + publicWarp.getName();
	        Permission publicWarpPermission = new Permission(permString, PermissionDefault.TRUE);
	        publicWarpChildren.put(permString, true);
	        pm.addPermission(publicWarpPermission);
	    }
	    Permission warpAccessPublicPerm = new Permission("openwarp.warp.access.public.*", PermissionDefault.TRUE, publicWarpChildren);
	    pm.addPermission(warpAccessPublicPerm);
	    
	    // The same, for private warps
	    Map<String, Boolean> privateWarpChildren = new HashMap<String, Boolean>();
	    for(String playerName : this.getPrivateWarps().keySet()) {
	        String permPrefix = "openwarp.warp.access.private." + playerName;
	        privateWarpChildren.put(permPrefix + ".*", true);
	        
	        Map<String, Boolean> privateWarpSubchildren = new HashMap<String, Boolean>();
	        for(Warp privateWarp : this.getPrivateWarps(playerName).values()) {
	            String permString = permPrefix + "." + privateWarp.getName();
	            Permission privateWarpPermission = new Permission(permString, PermissionDefault.TRUE);
	            privateWarpSubchildren.put(permString, true);
	            pm.addPermission(privateWarpPermission);
	        }
	        Permission warpAccessPrivateSubperm = new Permission(permPrefix + ".*", privateWarpSubchildren);
	        pm.addPermission(warpAccessPrivateSubperm);
	    }
	    Permission warpAccessPrivatePerm = new Permission("openwarp.warp.access.private.*", PermissionDefault.TRUE, privateWarpChildren);
	    pm.addPermission(warpAccessPrivatePerm);
	    
	    // Put the actual access perms in
	    Map<String, Boolean> accessChildren = new HashMap<String, Boolean>() {{ put("openwarp.warp.access.public.*", true); put("openwarp.warp.access.private.*", true); }};
	    Permission warpAccessPerm = new Permission("openwarp.warp.access.*", PermissionDefault.TRUE, accessChildren);
	    pm.addPermission(warpAccessPerm);
	    
	    // Make the primary access perm a child of overall warp perms
	    Permission warpPerm = pm.getPermission("openwarp.warp.*");
	    if(warpPerm != null) {
	        pm.getPermission("openwarp.warp.*").getChildren().put("openwarp.warp.access.*", true);
	        pm.getPermission("openwarp.warp.*").recalculatePermissibles();
	    } else {
	        LOG.severe(LOG_PREFIX + "Error inserting warp access permissions. This is a bug!");
	    }
	}
	
	/**
	 * Create home permission nodes for all loaded homes.
	 */
	public void loadHomePermissions() {
	    PluginManager pm = this.getServer().getPluginManager();
	    
	    Map<String, Boolean> homeAccessChildren = new HashMap<String, Boolean>();
	    for(Entry<String, Location> entry : this.getHomes().entrySet()) {
	        String playerName = entry.getKey();
	        String permString = "openwarp.home.access." + playerName;
	        
	        Permission homeAccessPerm = new Permission(permString, PermissionDefault.OP);
	        homeAccessChildren.put(permString, true);
	        
	        pm.addPermission(homeAccessPerm);
	    }
	    
	    Permission homeAccessPerm = new Permission("openwarp.home.access.*", PermissionDefault.OP, homeAccessChildren);
	    pm.addPermission(homeAccessPerm);
	    homeAccessPerm.recalculatePermissibles();
	    
	    Permission homePerm = pm.getPermission("openwarp.home.*");
	    if(homePerm != null) {
	        homePerm.getChildren().put("openwarp.home.access.*", true);
	        homePerm.recalculatePermissibles();
	    } else {
	        LOG.severe(LOG_PREFIX + "Could not locate master home permission node. This is a bug.");
	    }
	}
	
	private void loadCommands() {
		this.commandHandler = new CommandHandler(this, this.permissionsHandler);
		
		this.commandHandler.registerCommand(new OWWarpCommand(this));
		this.commandHandler.registerCommand(new OWWarpListCommand(this));
		this.commandHandler.registerCommand(new OWWarpDetailCommand(this));
		this.commandHandler.registerCommand(new OWWarpSetCommand(this));
		this.commandHandler.registerCommand(new OWWarpDeleteCommand(this));
		
		this.commandHandler.registerCommand(new OWHomeCommand(this));
		this.commandHandler.registerCommand(new OWHomeSetCommand(this));
		
		this.commandHandler.registerCommand(new OWQuotaShowCommand(this));
		this.commandHandler.registerCommand(new OWQuotaUsageCommand(this));
		this.commandHandler.registerCommand(new OWQuotaSetCommand(this));
		
		this.commandHandler.registerCommand(new OWStackPushCommand(this));
		this.commandHandler.registerCommand(new OWStackPopCommand(this));
		this.commandHandler.registerCommand(new OWStackPeekCommand(this));
		this.commandHandler.registerCommand(new OWStackPrintCommand(this));
		
		this.commandHandler.registerCommand(new OWTopCommand(this));
		this.commandHandler.registerCommand(new OWJumpCommand(this));
		this.commandHandler.registerCommand(new OWBackCommand(this));
	}
	
	private void loadListeners() {
	    OWPlayerListener playerListener = new OWPlayerListener(this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
        
        OWEntityListener entityListener = new OWEntityListener(this);
        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
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
		return this.commandHandler.locateAndRunCommand(sender, keyPath);
	}
	
	public Map<String, Warp> getPublicWarps() {
	    return this.publicWarps;
	}
	
	public Map<String, Map<String, Warp>> getPrivateWarps() {
	    return this.privateWarps;
	}
	
	public Map<String, Warp> getPrivateWarps(String playerName) {
        return this.getPrivateWarps().get(playerName);
    }
	
	public Map<String, Location> getHomes() {
	    return this.homes;
	}
	
	public OWLocationTracker getLocationTracker() {
	    return this.locationTracker;
	}
	
	public OWQuotaManager getQuotaManager() {
        return this.quotaManager;
    }
	
	public OWPermissionsHandler getPermissionsHandler() {
	    return this.permissionsHandler;
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
        if(sender instanceof Player) {
            Player player = (Player)sender;
            for(Entry<String, Warp> entry : this.getPrivateWarps().get(player.getName()).entrySet()) {
                String name = entry.getKey();
                if(name.equalsIgnoreCase(warpName)) {
                    return entry.getValue();
                }
            }
        }
        
        // No match
        return null;
    }
    
    /**
     * Get the Warp, if any, matching the given Location for the given sender.
     * 
     * @param sender The sender for whom to check for warps
     * @param location The location of the warp to find
     * @return The matching warp, if found
     * @see getWarp(CommandSender, String)
     */
    public Warp getWarp(CommandSender sender, Location location) {
        // First check public warps
        for(Entry<String, Warp> entry : this.getPublicWarps().entrySet()) {
            Location warpLoc = entry.getValue().getLocation();
            if(location.equals(warpLoc)) {
                return entry.getValue();
            }
        }
        
        // If no match, check private warps
        if(sender instanceof Player) {
            Player player = (Player)sender;
            for(Entry<String, Warp> entry : this.getPrivateWarps().get(player.getName()).entrySet()) {
                Location warpLoc = entry.getValue().getLocation();
                if(location.equals(warpLoc)) {
                    return entry.getValue();
                }
            }
        }
        
        // No match
        return null;
    }

}
