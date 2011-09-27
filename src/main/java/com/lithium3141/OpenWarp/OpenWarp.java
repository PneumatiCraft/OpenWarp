package com.lithium3141.OpenWarp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
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
import com.lithium3141.OpenWarp.util.MVConnector;
import com.lithium3141.OpenWarp.util.StringUtil;
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
    public static final Logger DEBUG_LOG = Logger.getLogger("OpenWarpDebug");
	
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
    public static final String BACK_KEY = "back";
    public static final String STACK_KEY = "stack";
    public static final String MULTIWORLD_HOMES_KEY = "multiworld_homes";
    public static final String DEBUG_KEY = "debug";

	// Global configuration variables
	public Configuration configuration;
	private Map<String, OWPlayerConfiguration> playerConfigs = new HashMap<String, OWPlayerConfiguration>(); // player name => config
	
	public Configuration publicWarpsConfig;
	private Map<String, Warp> publicWarps = new HashMap<String, Warp>(); // warp name => warp
	private Map<String, Map<String, Warp>> privateWarps = new HashMap<String, Map<String, Warp>>(); // player name => (warp name => warp)
	private Map<String, Map<String, Location>> homes = new HashMap<String, Map<String, Location>>(); // player name => (world name => home), world name == null implies "default" home
	
	private OWQuotaManager quotaManager;
	
	private OWPermissionsHandler permissionsHandler;

    // Supported commands
	private CommandHandler commandHandler;
	
	// Per-player data
	private OWLocationTracker locationTracker;

	@Override
	public void onDisable() {
		this.saveAllConfigurations();
		
		LOG.info(LOG_PREFIX + "Disabled!");
	}
	
	/**
	 * Save all configuration files currently loaded, including global
	 * warp and quota configurations and configurations for each player.
	 */
	public void saveAllConfigurations() {
	    if(this.configuration != null) {
            this.saveGlobalConfiguration();
            
            // Save player-specific data
            for(String playerName : this.playerConfigs.keySet()) {
                this.savePrivateConfiguration(playerName);
            }
        }
	}
	
	/**
	 * Save global configurations and the individual configuration for the
	 * provided player.
	 * 
	 * @param player the Player to save configurations for.
	 */
	public void saveConfigurations(Player player) {
	    this.saveGlobalConfiguration();
	    this.savePrivateConfiguration(player.getName());
	}
	 
	/**
     * Save global configurations and the individual configuration for the
     * provided player.
     * 
     * @param playerName the name of the player to save configurations for.
     */
	public void saveConfigurations(String playerName) {
	    this.saveGlobalConfiguration();
	    this.savePrivateConfiguration(playerName);
	}
	
	private void saveGlobalConfiguration() {
	    if(this.configuration != null) {
	        // Save overall configuration
            DEBUG_LOG.fine("Writing global player name list with " + this.playerConfigs.keySet().size() + " elements");
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

            // Save flags
            this.configuration.setProperty(MULTIWORLD_HOMES_KEY, this.configuration.getBoolean(DEBUG_KEY, false));
            this.configuration.setProperty(DEBUG_KEY, this.configuration.getBoolean(DEBUG_KEY, false));
	    }
	}
	
	private void savePrivateConfiguration(String playerName) {
	    if(this.configuration != null) {
	        OWPlayerConfiguration config = this.playerConfigs.get(playerName);
	        
	        if(config != null && !config.save()) {
                LOG.warning(LOG_PREFIX + " - Couldn't save configuration for player " + config.getPlayerName() + "; continuing...");
            }
	    }
	}

	@Override
	public void onEnable() {
		// Set up configuration folder if necessary
		this.getDataFolder().mkdirs();

		// Create overall permission
		this.getServer().getPluginManager().addPermission(new Permission("openwarp.*", PermissionDefault.OP));
		Permission wildcardPerm = this.getServer().getPluginManager().getPermission("*");
		if(wildcardPerm != null) {
		    wildcardPerm.getChildren().put("openwarp.*", true);
		    wildcardPerm.recalculatePermissibles();
		}
		
		// Get configuration file (even if nonexistent)
		this.configuration = new Configuration(new File(this.getDataFolder(), MASTER_CONFIG_FILENAME));
		this.configuration.load();
		
		this.publicWarpsConfig = new Configuration(new File(this.getDataFolder(), PUBLIC_WARP_CONFIG_FILENAME));
		this.publicWarpsConfig.load();
		
		// Start location tracking
		this.locationTracker = new OWLocationTracker(this);
		
        // Initialize debug log
        this.setupDebugLog();
		
		// Read warp names
        this.loadWarps(this.publicWarpsConfig, this.publicWarps);
        
        // Instantiate quota manager, permissions - need them for player configs
        this.quotaManager = new OWQuotaManager(this);
        this.permissionsHandler = new OWPermissionsHandler(this);

        // Read quotas
        this.quotaManager.loadGlobalQuotas(this.configuration);
        
		// Read player names and create configurations for each
		List<String> playerNames = this.configuration.getStringList(PLAYER_NAMES_LIST_KEY, new ArrayList<String>());
		for(String playerName : playerNames) {
			this.registerPlayerName(playerName);
		}
		
		// Set up supported commands
		this.loadCommands();
		
		// Instantiate permission nodes for all relevant objects
		this.loadWarpPermissions();
		this.loadHomePermissions();
		
		// Start listening for events
		this.loadListeners();
		
		// Enable Multiverse Support
		this.enableMultiverseSupport();
		
		LOG.info(LOG_PREFIX + "Enabled version " + this.getDescription().getVersion());
	}

    private void setupDebugLog() {
        boolean useDebug = this.configuration.getBoolean(DEBUG_KEY, false);
        Level logLevel = (useDebug ? Level.FINEST : Level.OFF);

        DEBUG_LOG.setLevel(logLevel);
        OWDebugHandler debugHandler = new OWDebugHandler(new File(this.getDataFolder(), "debug.log"));
        debugHandler.setLevel(logLevel);
        DEBUG_LOG.addHandler(debugHandler);

        DEBUG_LOG.fine("Enabled debug log at " + (new Date()).toString());
    }

    private void enableMultiverseSupport() {
        new MVConnector(this.getServer().getPluginManager().getPlugin("Multiverse-Core"));
        System.out.println("[OpenWarp] Found Multiverse 2, Support Enabled.");
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
	    for(String playerName : this.homes.keySet()) {
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
        this.commandHandler.registerCommand(new OWWarpShareCommand(this));
        this.commandHandler.registerCommand(new OWWarpUnshareCommand(this));
		
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
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        
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
			keyPath.add(args[i]);
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
        if(sender instanceof Player) {
            DEBUG_LOG.finer(((Player)sender).getName() + " requests warp '" + warpName + "'");
        }

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

        // If still no match, check shared warps
        if(warpName.contains(":") && (sender instanceof Player)) {
            String requester = ((Player)sender).getName();
            String[] parts = warpName.split(":");
            String recipient = parts[0];
            warpName = StringUtil.arrayJoin(Arrays.copyOfRange(parts, 1, parts.length), ":");
            DEBUG_LOG.finest("Checking shared warps; want player '" + recipient + "' and warp '" + warpName + "'");

            for(Entry<String, Map<String, Warp>> mapEntry : this.getPrivateWarps().entrySet()) {
                if(mapEntry.getKey().equalsIgnoreCase(recipient)) {
                    for(Entry<String, Warp> entry : mapEntry.getValue().entrySet()) {
                        if(entry.getKey().equalsIgnoreCase(warpName)) {
                            Warp warp = entry.getValue();
                            if(warp.isInvited(requester)) {
                                return warp;
                            }
                        }
                    }
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
     * @see #getWarp(CommandSender, String)
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

        // If still no match, check shared warps
        if(sender instanceof Player) {
            Player player = (Player)sender;
            for(Entry<String, Map<String, Warp>> mapEntry : this.getPrivateWarps().entrySet()) {
                String recipient = mapEntry.getKey();
                if(recipient.equals(player.getName())) {
                    continue;
                }
                for(Entry<String, Warp> entry : this.getPrivateWarps().get(recipient).entrySet()) {
                    Warp warp = entry.getValue();
                    if(location.equals(warp.getLocation()) && warp.isInvited(player)) {
                        return warp;
                    }
                }
            }
        }

        // No match
        return null;
    }

    /**
     * Get the set of homes for the given player. Fetches all homes for the player
     * across every world. This method is mostly useful in configuration writing
     * and printing information about all a player's homes.
     *
     * @param playerName The player for whom to fetch world-specific homes.
     * @return A Map of world names to home Location instances, or null if OpenWarp
     * is not using multiworld homes.
     */
    public Map<String, Location> getWorldHomes(String playerName) {
        if(!this.usingMultiworldHomes()) {
            return null;
        }

        return this.homes.get(playerName);
    }

    /**
     * Get the home for the given player in the given world. If no such home for the player
     * exists in the specified world, returns the default home for that player; if that
     * home does not exist, returns null. Notably, this method will not search other worlds
     * for a home if no default home is set.
     *
     * If OpenWarp is configured to not use multiworld homes, this method always returns
     * the default home.
     *
     * @param playerName The player for whom to fetch a home.
     * @param worldName The world within which to search for the player's home.
     * @return A Location for the located home or null if no such home exists.
     */
    public Location getHome(String playerName, String worldName) {
        if(this.usingMultiworldHomes()) {
            // Multiworld homes - get home for given world name
            DEBUG_LOG.fine("Fetching home for player '" + playerName + "' in world '" + worldName + "'");
            this.debugHomes();
            
            if(!this.homes.containsKey(playerName)) {
                DEBUG_LOG.finer("    ...no such player");
                return null;
            }

            Map<String, Location> playerHomes = this.homes.get(playerName);
            if(playerHomes == null) {
                DEBUG_LOG.finer("    ...player registered, but has no homes map");
                return null;
            }

            if(playerHomes.containsKey(worldName)) {
                DEBUG_LOG.finer("    ...located specific warp in world");
                return playerHomes.get(worldName);
            } else {
                DEBUG_LOG.finer("    ...no specific warp; returning default warp");
                return playerHomes.get(null);
            }
        } else {
            // No multiworld homes - fetch default home
            if(!this.homes.containsKey(playerName)) {
                return null;
            }

            Map<String, Location> playerHomes = this.homes.get(playerName);
            if(playerHomes == null) {
                return null;
            }

            return playerHomes.get(null);
        }
    }

    public Location getHome(Player player, String worldName) {
        return this.getHome(player.getName(), worldName);
    }

    public Location getHome(String playerName, World world) {
        return this.getHome(playerName, world.getName());
    }

    public Location getHome(Player player, World world) {
        return this.getHome(player.getName(), world.getName());
    }

    /**
     * Get the default home for the given player.
     *
     * @param playerName The player for whom to fetch a home.
     * @return The default home for the player, or null if none is set.
     */
    public Location getDefaultHome(String playerName) {
        return this.getHome(playerName, (String)null);
    }

    public Location getDefaultHome(Player player) {
        return this.getDefaultHome(player.getName());
    }

    /**
     * Set the home for the given player in the given world. TODO finish this doc.
     *
     * @param playerName The player for whom to set the home.
     * @param worldName The world in which to set the home; use null for default.
     * @param home The new Location to use for the home.
     * @return The Location being replaced, if any; null otherwise.
     */
    public Location setHome(String playerName, String worldName, Location home) {
        if(this.usingMultiworldHomes()) {
            // Multiworld homes - save home under world name key
            DEBUG_LOG.fine("Setting home for player '" + playerName + "' in world '" + worldName + "'");

            if(!this.homes.containsKey(playerName)) {
                DEBUG_LOG.finer("    ...adding new player map");
                this.homes.put(playerName, new HashMap<String, Location>());
            }

            if(this.homes.get(playerName).size() == 0) {
                this.homes.get(playerName).put(null, home);
            }
            return this.homes.get(playerName).put(worldName, home);
        } else {
            // No multiworld - place the home as the default (null key)
            if(!this.homes.containsKey(playerName)) {
                this.homes.put(playerName, new HashMap<String, Location>());
            }
            return this.homes.get(playerName).put(null, home);
        }
    }

    public Location setHome(Player player, World world, Location home) {
        return this.setHome(player.getName(), world.getName(), home);
    }

    public Location setDefaultHome(String playerName, Location home) {
        return this.setHome(playerName, null, home);
    }

    public Location setDefaultHome(Player player, Location home) {
        return this.setDefaultHome(player.getName(), home);
    }

    // TODO this method should go away.
    private void debugHomes() {
        DEBUG_LOG.fine("Homes:");
        for(String playerName : this.homes.keySet()) {
            DEBUG_LOG.fine("    " + playerName + ":");
            for(String worldName : this.homes.get(playerName).keySet()) {
                Location home = this.homes.get(playerName).get(worldName);
                DEBUG_LOG.fine("        " + worldName + ": (" + home.getX() + "," + home.getY() + "," + home.getZ() + ")");
            }
        }
    }

    /**
     * Check whether this instance of OpenWarp is configured to use multiworld
     * homes. Multiworld homes allow users to set a home in each world, rather than
     * one overall; this feature is configured in the plugin YAML file.
     *
     * @return True if OpenWarp is using multiworld homes; false otherwise.
     */
    public boolean usingMultiworldHomes() {
        return this.configuration.getBoolean(MULTIWORLD_HOMES_KEY, false);
    }

}
