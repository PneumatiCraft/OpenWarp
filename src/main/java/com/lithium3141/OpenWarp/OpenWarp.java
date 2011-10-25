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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.lithium3141.OpenWarp.config.OWConfigurationManager;
import com.lithium3141.OpenWarp.config.OWPlayerConfiguration;
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
	
	// Global configuration variables
    private OWConfigurationManager configurationManager;
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
		this.configurationManager.saveAllConfigurations();
		
		LOG.info(LOG_PREFIX + "Disabled!");
	}
	
	@Override
	public void onEnable() {
		// Create overall permission
		this.getServer().getPluginManager().addPermission(new Permission("openwarp.*", PermissionDefault.OP));
		Permission wildcardPerm = this.getServer().getPluginManager().getPermission("*");
		if(wildcardPerm != null) {
		    wildcardPerm.getChildren().put("openwarp.*", true);
		    wildcardPerm.recalculatePermissibles();
		}

        // Load configurations
        this.configurationManager = new OWConfigurationManager(this);
		
		// Start location tracking
		this.locationTracker = new OWLocationTracker(this);
		
        // Initialize debug log
        this.setupDebugLog();
		
		// Read warp names
        this.configurationManager.loadPublicWarps(this.publicWarps);
        
        // Instantiate quota manager, permissions - need them for player configs
        this.quotaManager = new OWQuotaManager(this);
        this.permissionsHandler = new OWPermissionsHandler(this);

        // Read quotas
        this.quotaManager.setGlobalPublicWarpQuota(this.configurationManager.readGlobalPublicWarpQuota());
        this.quotaManager.setGlobalPrivateWarpQuota(this.configurationManager.readGlobalPrivateWarpQuota());
        
		// Read player names and create configurations for each
        this.configurationManager.loadPlayers();
		
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
        boolean useDebug = this.configurationManager.readDebug();
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

    public OWConfigurationManager getConfigurationManager() {
        return this.configurationManager;
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

        // If still no match, try to cast to coords
        if(sender instanceof Player) {
            Pattern coordPattern = Pattern.compile("(?:([a-zA-Z0-9_]+):)?(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)"); // it burns us
            Matcher coordMatcher = coordPattern.matcher(warpName);
            if(coordMatcher.matches()) {
                String worldName = coordMatcher.group(1);
                int x = Integer.parseInt(coordMatcher.group(2));
                int y = Integer.parseInt(coordMatcher.group(3));
                int z = Integer.parseInt(coordMatcher.group(4));

                World world;
                if(worldName == null) {
                    world = ((Player)sender).getWorld();
                } else {
                    world = this.getServer().getWorld(worldName);
                }
                System.out.println("DEBUG: warping exact to world " + world.getName());
                if(world != null) {
                    return new Warp(this, "_EXACT", new Location(world, (double)x, (double)y, (double)z), ((Player)sender).getName());
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
        if(!this.configurationManager.usingMultiworldHomes()) {
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
        if(this.configurationManager.usingMultiworldHomes()) {
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
        if(this.configurationManager.usingMultiworldHomes()) {
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

}
