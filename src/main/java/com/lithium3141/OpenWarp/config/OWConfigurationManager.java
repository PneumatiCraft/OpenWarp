package com.lithium3141.OpenWarp.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.OWQuotaManager;
import com.lithium3141.OpenWarp.Warp;

/**
 * Container class for OpenWarp configuration information. Manages both the
 * global configuration and individual player configuration files; responsible
 * for load and save operations.
 *
 * @author lithium3141
 */
public class OWConfigurationManager {

    /**
     * The OpenWarp instance backing this configuration manager.
     */
    private OpenWarp plugin;

    /**
     * The filename to use for global plugin configuration.
     */
    public static final String MASTER_CONFIG_FILENAME = "config.yml";

    /**
     * The filename to use for public warps.
     */
    public static final String PUBLIC_WARP_CONFIG_FILENAME = "warps.yml";

    /**
     * The YAML key for lists of player names.
     */
    public static final String PLAYER_NAMES_LIST_KEY = "players";

    /**
     * The YAML key for lists of warps.
     */
    public static final String WARPS_LIST_KEY = "warps";

    /**
     * The YAML key for warp quotas.
     */
    public static final String QUOTAS_KEY = "quotas";

    /**
     * The YAML key for a public warp quota.
     */
    public static final String QUOTA_PUBLIC_KEY = "public";

    /**
     * The YAML key for a private warp quota.
     */
    public static final String QUOTA_PRIVATE_KEY = "private";

    /**
     * The YAML key for a player's home location.
     */
    public static final String HOME_KEY = "home";

    /**
     * The YAML key for a player's previous location.
     */
    public static final String BACK_KEY = "back";

    /**
     * The YAML key for a player's location stack.
     */
    public static final String STACK_KEY = "stack";

    /**
     * The YAML key for whether multiworld homes are enabled.
     */
    public static final String MULTIWORLD_HOMES_KEY = "multiworld_homes";

    /**
     * The YAML key for debug information.
     */
    public static final String DEBUG_KEY = "debug";

    /**
     * The Configuration object representing global plugin configuration.
     */
    private Configuration configuration;

    /**
     * The set of player configuration objects, mapped to by player names.
     */
    private Map<String, OWPlayerConfiguration> playerConfigs = new HashMap<String, OWPlayerConfiguration>();

    /**
     * The Configuration object representing public warp information.
     */
    private Configuration publicWarpsConfig;

    /**
     * Create a new OWConfigurationManager backed by the given OpenWarp instance.
     * Sets up data folders on-disk and loads (creating if necessary) the global
     * configuration and public warp files.
     *
     * @param ow The OpenWarp instance backing this OWConfigurationManager.
     */
    public OWConfigurationManager(OpenWarp ow) {
        this.plugin = ow;

        // Set up configuration folder if necessary
        this.plugin.getDataFolder().mkdirs();

        // Get configuration file (even if nonexistent)
        this.configuration = new Configuration(new File(this.plugin.getDataFolder(), MASTER_CONFIG_FILENAME));
        this.configuration.load();

        this.publicWarpsConfig = new Configuration(new File(this.plugin.getDataFolder(), PUBLIC_WARP_CONFIG_FILENAME));
        this.publicWarpsConfig.load();
    }

    /**
     * Save all configuration files currently loaded, including global
     * warp and quota configurations and configurations for each player.
     * Calls #saveGlobalConfiguration() and #savePlayerConfiguration(String)
     * internally.
     *
     * @see #saveGlobalConfiguration()
     * @see #savePlayerConfiguration(String)
     */
    public void saveAllConfigurations() {
        OpenWarp.DEBUG_LOG.fine("Writing ALL OpenWarp configuration files");
        if(this.configuration != null) {
            this.saveGlobalConfiguration();

            // Save player-specific data
            for(String playerName : this.playerConfigs.keySet()) {
                this.savePlayerConfiguration(playerName);
            }
        }
    }

    /**
     * Save global configuration data, including the files <tt>warps.yml</tt>
     * and <tt>config.yml</tt> in the primary OpenWarp directory. Writes all
     * YAML nodes into those files from current in-memory sets. Currently does
     * no checking about whether a write is necessary.
     */
    public void saveGlobalConfiguration() {
        OpenWarp.DEBUG_LOG.fine("Writing OpenWarp global configuration file");

        if(this.configuration != null) {
            // Save overall configuration
            OpenWarp.DEBUG_LOG.fine("Writing global player name list with " + this.playerConfigs.keySet().size() + " elements");
            this.configuration.setProperty(PLAYER_NAMES_LIST_KEY, new ArrayList<String>(this.playerConfigs.keySet()));
            if(!this.configuration.save()) {
                OpenWarp.LOG.warning(OpenWarp.LOG_PREFIX + "Couldn't save player list; continuing...");
            }

            // Save public warps
            Map<String, Object> warps = new HashMap<String, Object>();
            for(Entry<String, Warp> entry : this.plugin.getPublicWarps().entrySet()) {
                warps.put(entry.getKey(), entry.getValue().getConfigurationMap());
            }

            this.publicWarpsConfig.setProperty(WARPS_LIST_KEY, warps);
            if(!this.publicWarpsConfig.save()) {
                OpenWarp.LOG.warning(OpenWarp.LOG_PREFIX + "Couldn't save public warp list; continuing...");
            }

            // Save global quotas
            this.configuration.setProperty(QUOTAS_KEY, this.plugin.getQuotaManager().getGlobalQuotaMap());

            // Save flags
            this.configuration.setProperty(MULTIWORLD_HOMES_KEY, this.configuration.getBoolean(DEBUG_KEY, false));
            this.configuration.setProperty(DEBUG_KEY, this.configuration.getBoolean(DEBUG_KEY, false));
        }
    }

    /**
     * Save the player-specific configuration files for the given Player.
     * Calls #savePlayerConfiguration(String) internally.
     *
     * @param player The Player for whom to save configuration data.
     * @see #savePlayerConfiguration(String)
     */
    public void savePlayerConfiguration(Player player) {
        this.savePlayerConfiguration(player.getName());
    }

    /**
     * Save the player-specific configuration files for the given player.
     * Writes files <tt>general.yml</tt>, <tt>quota.yml</tt>, and <tt>warps.yml</tt>
     * into the OpenWarp subdirectory named for the player. Writes all YAML nodes
     * into those files from current in-memory sets. Currently does no checking about
     * whether such a write is necessary.
     *
     * @param playerName The name of the player for whom to save configuration data.
     */
    public void savePlayerConfiguration(String playerName) {
        OpenWarp.DEBUG_LOG.fine("Writing OpenWarp player configuration file (" + playerName + ")");

        if(this.configuration != null) {
            OWPlayerConfiguration config = this.playerConfigs.get(playerName);

            if(config != null && !config.save()) {
                OpenWarp.LOG.warning(OpenWarp.LOG_PREFIX + " - Couldn't save configuration for player " + config.getPlayerName() + "; continuing...");
            }
        }
    }


    /**
     * Load public warps into the given map. Mutates the `target` argument.
     *
     * @param target The map into which to load new Warp objects.
     */
    public void loadPublicWarps(Map<String, Warp> target) {
        this.loadWarps(this.publicWarpsConfig, target);
    }

    /**
     * Load warp information from the given Configuration into the given Map.
     * Mutates the `target` argument.
     *
     * @param config The Configuration from which to read warps
     * @param target The Map into which to place Warp instances
     */
    public void loadWarps(Configuration config, Map<String, Warp> target) {
        List<String> keys = config.getKeys(WARPS_LIST_KEY);
        if(keys != null) {
            for(String key : keys) {
                ConfigurationNode node = config.getNode(WARPS_LIST_KEY + "." + key);
                Warp warp = new Warp(this.plugin, key, node);
                target.put(warp.getName(), warp);
            }
        }
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
            OWPlayerConfiguration playerConfig = new OWPlayerConfiguration(this.plugin, playerName);
            playerConfig.load();
            this.playerConfigs.put(playerName, playerConfig);
        }
    }

    /**
     * Load player information from disk, creating OWPlayerConfiguration instances
     * for each.
     */
    public void loadPlayers() {
        List<String> playerNames = this.configuration.getStringList(PLAYER_NAMES_LIST_KEY, new ArrayList<String>());
        for(String playerName : playerNames) {
            this.registerPlayerName(playerName);
        }
    }

    /**
     * Read the global quota for public warps from disk.
     *
     * @return The global public warp quota.
     */
    public int readGlobalPublicWarpQuota() {
        return this.configuration.getInt(QUOTAS_KEY + "." + QUOTA_PUBLIC_KEY, OWQuotaManager.QUOTA_UNDEFINED);
    }

    /**
     * Read the global quota for private warps from disk.
     *
     * @return The global private warp quota.
     */
    public int readGlobalPrivateWarpQuota() {
        return this.configuration.getInt(QUOTAS_KEY + "." + QUOTA_PRIVATE_KEY, OWQuotaManager.QUOTA_UNDEFINED);
    }

    /**
     * Read the debug flag from disk.
     *
     * @return Whether this instance of OpenWarp should enable debug logging.
     */
    public boolean readDebug() {
        return this.configuration.getBoolean(DEBUG_KEY, false);
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
