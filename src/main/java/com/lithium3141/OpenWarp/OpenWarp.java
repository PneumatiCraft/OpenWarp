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

import com.lithium3141.OpenWarp.commands.*;
import com.lithium3141.OpenWarp.config.OWConfigurationManager;
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

    /**
     * Logger object for all Minecraft-based messages.
     */
    public static final Logger LOG = Logger.getLogger("Minecraft");

    /**
     * Prefix string for every log message output by this plugin.
     */
    public static final String LOG_PREFIX = "[OpenWarp] ";

    /**
     * Logger object for debug-level messages not sent to general Minecraft logging.
     */
    public static final Logger DEBUG_LOG = Logger.getLogger("OpenWarpDebug");

    // Global configuration variables

    /**
     * Object managing plugin configuration files and player information.
     */
    private OWConfigurationManager configurationManager;

    /**
     * Public warps tracked by this plugin. Maps warp names to their corresponding
     * Warp objects.
     */
    private Map<String, Warp> publicWarps = new HashMap<String, Warp>();

    /**
     * Private warps tracked by this plugin. Maps warp names to their corresponding
     * Warp objects for each player name.
     */
    private Map<String, Map<String, Warp>> privateWarps = new HashMap<String, Map<String, Warp>>();

    /**
     * Homes tracked by this plugin. Maps world names to their corresponding home
     * Location objects for each player name.
     */
    private Map<String, Map<String, Location>> homes = new HashMap<String, Map<String, Location>>();

    /**
     * Object managing warp quota information for this plugin.
     */
    private OWQuotaManager quotaManager;

    /**
     * Object managing permissions calls for this plugin. Handles both SuperPerms
     * and Permissions 2.x/3.x checks.
     */
    private OWPermissionsHandler permissionsHandler;

    // Supported commands

    /**
     * Object managing commands and action dispatch for this plugin.
     */
    private CommandHandler commandHandler;

    // Per-player data

    /**
     * Object tracking individual player locations for history purposes.
     */
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
        if (wildcardPerm != null) {
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

    /**
     * Initialize the debugging log for output. The debug log is used for finer-grained
     * messages that do not need to go to the general output, and is generally written
     * to a File.
     */
    private void setupDebugLog() {
        boolean useDebug = this.configurationManager.readDebug();
        Level logLevel = (useDebug ? Level.FINEST : Level.OFF); // SUPPRESS CHECKSTYLE AvoidInlineConditionalsCheck

        DEBUG_LOG.setLevel(logLevel);
        OWDebugHandler debugHandler = new OWDebugHandler(new File(this.getDataFolder(), "debug.log"));
        debugHandler.setLevel(logLevel);
        DEBUG_LOG.addHandler(debugHandler);

        DEBUG_LOG.fine("Enabled debug log at " + (new Date()).toString());
    }

    /**
     * Check for the Multiverse plugin, and if it exists, enable support for accessing
     * OpenWarp locations as Multiverse destinations. See
     * {@link http://github.com/Multiverse/Multiverse-Core/wiki/Destinations}.
     */
    private void enableMultiverseSupport() {
        try {
            new MVConnector(this.getServer().getPluginManager().getPlugin("Multiverse-Core"));
            LOG.info(LOG_PREFIX + "Found Multiverse 2; `ow:` destination type support enabled.");
        } catch (Exception e) {
            LOG.warning(LOG_PREFIX + "Failed to enable Multiverse support!");
            LOG.warning(LOG_PREFIX + "You will be unable to use the Multiverse `ow:` destination type!");
        }
    }

    /**
     * Create warp permission nodes for all loaded warps.
     */
    public void loadWarpPermissions() {
        PluginManager pm = this.getServer().getPluginManager();

        // Finagle a new permission for public warps
        Map<String, Boolean> publicWarpChildren = new HashMap<String, Boolean>();
        for (Warp publicWarp : this.getPublicWarps().values()) {
            String permString = "openwarp.warp.access.public." + publicWarp.getName();
            Permission publicWarpPermission = new Permission(permString, PermissionDefault.TRUE);
            publicWarpChildren.put(permString, true);
            pm.addPermission(publicWarpPermission);
        }
        Permission warpAccessPublicPerm = new Permission("openwarp.warp.access.public.*", PermissionDefault.TRUE, publicWarpChildren);
        pm.addPermission(warpAccessPublicPerm);

        // The same, for private warps
        Map<String, Boolean> privateWarpChildren = new HashMap<String, Boolean>();
        for (String playerName : this.getPrivateWarps().keySet()) {
            String permPrefix = "openwarp.warp.access.private." + playerName;
            privateWarpChildren.put(permPrefix + ".*", true);

            Map<String, Boolean> privateWarpSubchildren = new HashMap<String, Boolean>();
            for (Warp privateWarp : this.getPrivateWarps(playerName).values()) {
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
        Map<String, Boolean> accessChildren = new HashMap<String, Boolean>() {{
            put("openwarp.warp.access.public.*", true);
            put("openwarp.warp.access.private.*", true);
        }};
        Permission warpAccessPerm = new Permission("openwarp.warp.access.*", PermissionDefault.TRUE, accessChildren);
        pm.addPermission(warpAccessPerm);

        // Also insert delete perms
        Map<String, Boolean> deletePublicChildren = new HashMap<String, Boolean>() {{
             put("openwarp.warp.delete.public.self", true);
             put("openwarp.warp.delete.public.other", true);
        }};
        Permission deletePublicPerm = new Permission("openwarp.warp.delete.public.*", PermissionDefault.TRUE, deletePublicChildren);
        pm.addPermission(deletePublicPerm);

        // Add public & private children of delete perm (which should already exist)
        Permission deletePerm = pm.getPermission("openwarp.warp.delete.*");
        if (deletePerm != null) {
            deletePerm.getChildren().put("openwarp.warp.delete.public.*", true);
            deletePerm.getChildren().put("openwarp.warp.delete.private.*", true);
            deletePerm.recalculatePermissibles();
        }

        // Make the primary access & delete perms a child of overall warp perms
        Permission warpPerm = pm.getPermission("openwarp.warp.*");
        if (warpPerm != null) {
            warpPerm.getChildren().put("openwarp.warp.access.*", true);
            warpPerm.getChildren().put("openwarp.warp.delete.*", true);
            warpPerm.recalculatePermissibles();
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
        for (String playerName : this.homes.keySet()) {
            String permString = "openwarp.home.access." + playerName;

            Permission homeAccessPerm = new Permission(permString, PermissionDefault.OP);
            homeAccessChildren.put(permString, true);

            pm.addPermission(homeAccessPerm);
        }

        Permission homeAccessPerm = new Permission("openwarp.home.access.*", PermissionDefault.OP, homeAccessChildren);
        pm.addPermission(homeAccessPerm);
        homeAccessPerm.recalculatePermissibles();

        Permission homePerm = pm.getPermission("openwarp.home.*");
        if (homePerm != null) {
            homePerm.getChildren().put("openwarp.home.access.*", true);
            homePerm.recalculatePermissibles();
        } else {
            LOG.severe(LOG_PREFIX + "Could not locate master home permission node. This is a bug.");
        }
    }

    /**
     * Initialize individual commands to be used by users of this plugin. An instance
     * of each command object must be created and registered with this plugin's
     * CommandHandler before it will have messages dispatched to it.
     */
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

        this.commandHandler.registerCommand(new OWTeleportCommand(this));
        this.commandHandler.registerCommand(new OWSummonCommand(this));
    }

    /**
     * Initialize listeners for in-game actions. An instance of each listener object
     * must be created and registered with the Bukkit server before it will respond
     * to events.
     */
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
        DEBUG_LOG.fine("Command received. Name:" + command.getName() + " label:" + command.getLabel() + " arglabel:" + commandLabel);

        // Construct a trie key path from the command label and args
        List<String> keyPath = new ArrayList<String>();
        keyPath.add(command.getLabel().toLowerCase());
        for (int i = 0; i < args.length; i++) {
            keyPath.add(args[i]);
        }

        // Locate and run the best matching command from the key path
        return this.commandHandler.locateAndRunCommand(sender, keyPath);
    }

    /**
     * Get all public warps known to this plugin.
     *
     * @return A map of warp names to their corresponding Warp objects.
     */
    public Map<String, Warp> getPublicWarps() {
        return this.publicWarps;
    }

    /**
     * Get all private warps known to this plugin.
     *
     * @return A map of player names to a map of warp names to their corresponding Warp objects.
     */
    public Map<String, Map<String, Warp>> getPrivateWarps() {
        return this.privateWarps;
    }

    /**
     * Get private warps for a particular player.
     *
     * @param playerName The name of the player for which to get public warps.
     * @return A map of warp names to their corresponding Warp objects, or null if the given
     *         player is not known to this plugin.
     */
    public Map<String, Warp> getPrivateWarps(String playerName) {
        return this.getPrivateWarps().get(playerName);
    }

    /**
     * Get the location tracker for this plugin.
     *
     * @return The OWLocationTracker instance watching players for this plugin.
     */
    public OWLocationTracker getLocationTracker() {
        return this.locationTracker;
    }

    /**
     * Get the quota manager for this plugin.
     *
     * @return The OWQuotaManager instance handling quota information for this plugin.
     */
    public OWQuotaManager getQuotaManager() {
        return this.quotaManager;
    }

    /**
     * Get the permissions handler for this plugin.
     *
     * @return The OWPermissionsHandler instance managing permissions checks for this plugin.
     */
    public OWPermissionsHandler getPermissionsHandler() {
        return this.permissionsHandler;
    }

    /**
     * Get the configuration handler for this plugin.
     *
     * @return The OWConfigurationManager instance handling on-disk configuration info for this plugin.
     */
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
     *          (3) the shared warp belonging to the given owner with the
     *          specified name, or (4) null.
     */
    public Warp getWarp(CommandSender sender, String warpName) {
        if (sender instanceof Player) {
            DEBUG_LOG.finer(((Player)sender).getName() + " requests warp '" + warpName + "'");
        }

        // First check public warps
        for (Entry<String, Warp> entry : this.getPublicWarps().entrySet()) {
            String name = entry.getKey();
            if (name.equalsIgnoreCase(warpName)) {
                return entry.getValue();
            }
        }

        // If no match, check private warps
        if (sender instanceof Player) {
            Player player = (Player)sender;
            for (Entry<String, Warp> entry : this.getPrivateWarps().get(player.getName()).entrySet()) {
                String name = entry.getKey();
                if (name.equalsIgnoreCase(warpName)) {
                    return entry.getValue();
                }
            }
        }

        // If still no match, check shared warps
        if (warpName.contains(":") && (sender instanceof Player)) {
            String requester = ((Player)sender).getName();
            String[] parts = warpName.split(":");
            String recipient = parts[0];
            warpName = StringUtil.arrayJoin(Arrays.copyOfRange(parts, 1, parts.length), ":");
            DEBUG_LOG.finest("Checking shared warps; want player '" + recipient + "' and warp '" + warpName + "'");

            for (Entry<String, Map<String, Warp>> mapEntry : this.getPrivateWarps().entrySet()) {
                if (mapEntry.getKey().equalsIgnoreCase(recipient)) {
                    for (Entry<String, Warp> entry : mapEntry.getValue().entrySet()) {
                        if (entry.getKey().equalsIgnoreCase(warpName)) {
                            Warp warp = entry.getValue();
                            if (warp.isInvited(requester)) {
                                return warp;
                            }
                        }
                    }
                }
            }
        }

        // If still no match, try to cast to coords
        if (sender instanceof Player) {
            Pattern coordPattern = Pattern.compile("(?:([a-zA-Z0-9_]+):)?(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)"); // it burns us
            Matcher coordMatcher = coordPattern.matcher(warpName);
            if (coordMatcher.matches()) {
                String worldName = coordMatcher.group(1);
                int x = Integer.parseInt(coordMatcher.group(2));
                int y = Integer.parseInt(coordMatcher.group(3)); // SUPPRESS CHECKSTYLE MagicNumberCheck
                int z = Integer.parseInt(coordMatcher.group(4)); // SUPPRESS CHECKSTYLE MagicNumberCheck

                World world;
                if (worldName == null) {
                    world = ((Player)sender).getWorld();
                } else {
                    world = this.getServer().getWorld(worldName);
                }
                System.out.println("DEBUG: warping exact to world " + world.getName());
                if (world != null) {
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
        for (Entry<String, Warp> entry : this.getPublicWarps().entrySet()) {
            Location warpLoc = entry.getValue().getLocation();
            if (location.equals(warpLoc)) {
                return entry.getValue();
            }
        }

        // If no match, check private warps
        if (sender instanceof Player) {
            Player player = (Player)sender;
            for (Entry<String, Warp> entry : this.getPrivateWarps().get(player.getName()).entrySet()) {
                Location warpLoc = entry.getValue().getLocation();
                if (location.equals(warpLoc)) {
                    return entry.getValue();
                }
            }
        }

        // If still no match, check shared warps
        if (sender instanceof Player) {
            Player player = (Player)sender;
            for (Entry<String, Map<String, Warp>> mapEntry : this.getPrivateWarps().entrySet()) {
                String recipient = mapEntry.getKey();
                if (recipient.equals(player.getName())) {
                    continue;
                }
                for (Entry<String, Warp> entry : this.getPrivateWarps().get(recipient).entrySet()) {
                    Warp warp = entry.getValue();
                    if (location.equals(warp.getLocation()) && warp.isInvited(player)) {
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
        if (!this.configurationManager.usingMultiworldHomes()) {
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
        if (this.configurationManager.usingMultiworldHomes()) {
            // Multiworld homes - get home for given world name
            DEBUG_LOG.fine("Fetching home for player '" + playerName + "' in world '" + worldName + "'");

            if (!this.homes.containsKey(playerName)) {
                DEBUG_LOG.finer("    ...no such player");
                return null;
            }

            Map<String, Location> playerHomes = this.homes.get(playerName);
            if (playerHomes == null) {
                DEBUG_LOG.finer("    ...player registered, but has no homes map");
                return null;
            }

            if (playerHomes.containsKey(worldName)) {
                DEBUG_LOG.finer("    ...located specific warp in world");
                return playerHomes.get(worldName);
            } else {
                DEBUG_LOG.finer("    ...no specific warp; returning default warp");
                return playerHomes.get(null);
            }
        } else {
            // No multiworld homes - fetch default home
            if (!this.homes.containsKey(playerName)) {
                return null;
            }

            Map<String, Location> playerHomes = this.homes.get(playerName);
            if (playerHomes == null) {
                return null;
            }

            return playerHomes.get(null);
        }
    }

    /**
     * Get the home for the given player in the given world.
     *
     * @param player The player for whom to fetch a home.
     * @param worldName The world within which to search for the player's home.
     * @return A Location for the located home or null if no such home exists.
     * @see #getHome(String, String)
     */
    public Location getHome(Player player, String worldName) {
        return this.getHome(player.getName(), worldName);
    }

    /**
     * Get the home for the given player in the given world.
     *
     * @param playerName The player for whom to fetch a home.
     * @param world The world within which to search for the player's home.
     * @return A Location for the located home or null if no such home exists.
     * @see #getHome(String, String)
     */
    public Location getHome(String playerName, World world) {
        return this.getHome(playerName, world.getName());
    }

    /**
     * Get the home for the given player in the given world.
     *
     * @param player The player for whom to fetch a home.
     * @param world The world within which to search for the player's home.
     * @return A Location for the located home or null if no such home exists.
     * @see #getHome(String, String)
     */
    public Location getHome(Player player, World world) {
        return this.getHome(player.getName(), world.getName());
    }

    /**
     * Get the default home for the given player.
     *
     * @param playerName The player for whom to fetch a home.
     * @return The default home for the player, or null if none is set.
     * @see #getHome(String, String)
     */
    public Location getDefaultHome(String playerName) {
        return this.getHome(playerName, (String)null);
    }

    /**
     * Get the default home for the given player.
     *
     * @param player The player for whom to fetch a home.
     * @return The default home for the player, or null if none is set.
     * @see #getHome(String, String)
     */
    public Location getDefaultHome(Player player) {
        return this.getDefaultHome(player.getName());
    }

    /**
     * Set the home for the given player in the given world. If multiworld homes are
     * enabled in the configuration, then the new home will be saved for the world
     * supplied; in addition, if this is the first home this player has specified,
     * the player's default home will be set as well. On the other hand, if multiworld
     * homes are not enabled, only the default home will be set.
     *
     * @param playerName The player for whom to set the home.
     * @param worldName The world in which to set the home; use null for default.
     * @param home The new Location to use for the home.
     * @return The Location being replaced, if any; null otherwise.
     */
    public Location setHome(String playerName, String worldName, Location home) {
        if (this.configurationManager.usingMultiworldHomes()) {
            // Multiworld homes - save home under world name key
            DEBUG_LOG.fine("Setting home for player '" + playerName + "' in world '" + worldName + "'");

            if (!this.homes.containsKey(playerName)) {
                DEBUG_LOG.finer("    ...adding new player map");
                this.homes.put(playerName, new HashMap<String, Location>());
            }

            if (this.homes.get(playerName).size() == 0) {
                this.homes.get(playerName).put(null, home);
            }
            return this.homes.get(playerName).put(worldName, home);
        } else {
            // No multiworld - place the home as the default (null key)
            if (!this.homes.containsKey(playerName)) {
                this.homes.put(playerName, new HashMap<String, Location>());
            }
            return this.homes.get(playerName).put(null, home);
        }
    }

    /**
     * Set the home for the given player in the given world.
     *
     * @param player The player for whom to set the home.
     * @param world The world in which to set the home; use null for default.
     * @param home The new Location to use for the home.
     * @return The Location being replaced, if any; null otherwise.
     * @see #setHome(String, String, Location)
     */
    public Location setHome(Player player, World world, Location home) {
        return this.setHome(player.getName(), world.getName(), home);
    }

    /**
     * Set the default home for the given player in the given world.
     *
     * @param playerName The player for whom to set the home.
     * @param home The new Location to use for the home.
     * @return The Location being replaced, if any; null otherwise.
     * @see #setHome(String, String, Location)
     */
    public Location setDefaultHome(String playerName, Location home) {
        return this.setHome(playerName, null, home);
    }

    /**
     * Set the default home for the given player in the given world.
     *
     * @param player The player for whom to set the home.
     * @param home The new Location to use for the home.
     * @return The Location being replaced, if any; null otherwise.
     * @see #setHome(String, String, Location)
     */
    public Location setDefaultHome(Player player, Location home) {
        return this.setDefaultHome(player.getName(), home);
    }

}
