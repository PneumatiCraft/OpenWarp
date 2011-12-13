package com.lithium3141.OpenWarp.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.OWQuotaManager;
import com.lithium3141.OpenWarp.Warp;

/**
 * Configuration for a single player. Encapsulates all on-disk info
 * for this player, including warps, quotas, etc.
 *
 * @author lithium3141
 */
public class OWPlayerConfiguration {

    // Configuration filenames
    /**
     * File containing general configuration for the player.
     */
    public static final String GENERAL_CONFIG_FILENAME = "general.yml";

    /**
     * File containing warp listings and information for the player.
     */
    public static final String WARP_CONFIG_FILENAME = "warps.yml";

    /**
     * File containing quota information for the player.
     */
    public static final String QUOTA_CONFIG_FILENAME = "quota.yml";

    /**
     * Temporary warp name to encapsulate the home location in a configuration file.
     */
    public static final String TEMP_HOME_NAME = "_HOME";

    /**
     * Temporary warp name to encapsulate the previous location in a configuration file.
     */
    public static final String TEMP_BACK_NAME = "_BACK";

    /**
     * Temporary warp name to encapsulate the warp stack in a configuration file.
     */
    public static final String TEMP_STACK_NAME = "_STACK";

    /**
     * The OpenWarp instance backing this player configuration.
     */
    private OpenWarp plugin;

    /**
     * The player name for whom this object holds configuration data.
     */
    private String playerName;

    /**
     * The directory holding configuration data for this object.
     */
    private File configFolder;

    /**
     * The Configuration object containing general player info.
     */
    private Configuration generalConfig;

    /**
     * The Configuration object containing player-specific warp info.
     */
    private Configuration warpConfig;

    /**
     * The Configuration object containing player-specific quota info.
     */
    private Configuration quotaConfig;

    /**
     * Construct a new player configuration for the given player name.
     *
     * @param ow The OpenWarp instance handling this player configuration
     * @param name The player to handle configuration for
     */
    public OWPlayerConfiguration(OpenWarp ow, String name) {
        this.plugin = ow;
        this.playerName = name;
    }

    /**
     * Construct a new player configuration for the given player.
     *
     * @param ow The OpenWarp instance handling this player configuration
     * @param player The player to handle configuration for
     */
    public OWPlayerConfiguration(OpenWarp ow, Player player) {
        this.plugin = ow;
        this.playerName = player.getName();
    }

    /**
     * Get the name of the player for which this class handles configuration.
     *
     * @return the player name for this configuration.
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Load this player configuration from disk.
     */
    public void load() {
        // Locate configs
        this.configFolder = new File(this.plugin.getDataFolder(), this.playerName);
        this.configFolder.mkdirs();

        // Build config objects from files
        this.generalConfig = new Configuration(new File(this.configFolder, GENERAL_CONFIG_FILENAME));
        this.warpConfig = new Configuration(new File(this.configFolder, WARP_CONFIG_FILENAME));
        this.quotaConfig = new Configuration(new File(this.configFolder, QUOTA_CONFIG_FILENAME));

        // Load configs
        this.generalConfig.load();
        this.warpConfig.load();
        this.quotaConfig.load();

        // Warps
        if (this.plugin.getPrivateWarps().get(this.playerName) == null) {
            this.plugin.getPrivateWarps().put(this.playerName, new HashMap<String, Warp>());
        }
        this.plugin.getConfigurationManager().loadWarps(this.warpConfig, this.plugin.getPrivateWarps().get(this.playerName));

        // Homes
        ConfigurationNode homeNode = this.generalConfig.getNode(OWConfigurationManager.HOME_KEY);
        if (homeNode != null) {
            this.plugin.setDefaultHome(this.playerName, new Warp(this.plugin, TEMP_HOME_NAME, homeNode).getLocation());
        }

        Map<String, ConfigurationNode> multiworldHomesMap = this.generalConfig.getNodes(OWConfigurationManager.MULTIWORLD_HOMES_KEY);
        if (multiworldHomesMap != null) {
            for (String worldName : multiworldHomesMap.keySet()) {
                this.plugin.setHome(this.playerName, worldName, new Warp(this.plugin, TEMP_HOME_NAME, multiworldHomesMap.get(worldName)).getLocation());
            }
        }

        // Back
        ConfigurationNode backNode = this.generalConfig.getNode(OWConfigurationManager.BACK_KEY);
        if (backNode != null) {
            this.plugin.getLocationTracker().setPreviousLocation(this.playerName, new Warp(this.plugin, TEMP_BACK_NAME, backNode).getLocation());
        }

        // Stack
        List<ConfigurationNode> warpStackNodes = this.generalConfig.getNodeList(OWConfigurationManager.STACK_KEY, new ArrayList<ConfigurationNode>());
        if (warpStackNodes != null) {
            Stack<Location> warpStack = new Stack<Location>();
            for (ConfigurationNode node : warpStackNodes) {
                warpStack.push(new Warp(this.plugin, TEMP_STACK_NAME, node).getLocation());
            }
            this.plugin.getLocationTracker().setLocationStack(this.playerName, warpStack);
        }

        // Quotas
        this.plugin.getQuotaManager().getPlayerMaxPublicWarps().put(this.playerName, this.quotaConfig.getInt(OWConfigurationManager.QUOTAS_KEY + "." + OWConfigurationManager.QUOTA_PUBLIC_KEY, OWQuotaManager.QUOTA_UNDEFINED));
        this.plugin.getQuotaManager().getPlayerMaxPrivateWarps().put(this.playerName, this.quotaConfig.getInt(OWConfigurationManager.QUOTAS_KEY + "." + OWConfigurationManager.QUOTA_PRIVATE_KEY, OWQuotaManager.QUOTA_UNDEFINED));
    }

    /**
     * Save this player configuration to disk.
     *
     * @return true if this player configuration was saved successfully
     *         or skipped; false on error.
     */
    public boolean save() {
        // Warps
        Map<String, Warp> playerWarps = this.plugin.getPrivateWarps(this.playerName);

        Map<String, Object> configWarps = new HashMap<String, Object>();
        for (Entry<String, Warp> entry : playerWarps.entrySet()) {
            configWarps.put(entry.getKey(), entry.getValue().getConfigurationMap());
        }
        this.warpConfig.setProperty(OWConfigurationManager.WARPS_LIST_KEY, configWarps);

        // Home
        if (this.plugin.getDefaultHome(this.playerName) != null) {
            Map<String, Object> homeWarpConfig = new Warp(this.plugin, TEMP_HOME_NAME, this.plugin.getDefaultHome(this.playerName), this.playerName).getConfigurationMap();
            if (homeWarpConfig != null) {
                this.generalConfig.setProperty(OWConfigurationManager.HOME_KEY, homeWarpConfig);
            } else {
                OpenWarp.LOG.warning(OpenWarp.LOG_PREFIX + "Not writing configuration for player " + this.playerName + " due to missing warp world");
                OpenWarp.LOG.warning(OpenWarp.LOG_PREFIX + "This may result in some data loss! Check the warp configuration for " + this.playerName);
                return true;
            }
        }

        Map<String, Location> worldHomes = this.plugin.getWorldHomes(this.playerName);
        if (worldHomes != null) {
            for (String worldName : worldHomes.keySet()) {
                if (worldName != null) {
                    Location worldHome = worldHomes.get(worldName);
                    String yamlKey = OWConfigurationManager.MULTIWORLD_HOMES_KEY + "." + worldName;

                    Map<String, Object> worldHomeWarpConfig = new Warp(this.plugin, TEMP_HOME_NAME, worldHome, this.playerName).getConfigurationMap();
                    if (worldHomeWarpConfig != null) {
                        this.generalConfig.setProperty(yamlKey, worldHomeWarpConfig);
                    } else {
                        OpenWarp.LOG.warning(OpenWarp.LOG_PREFIX + "Not writing configuration for player " + this.playerName + " due to broken multiworld home");
                        OpenWarp.LOG.warning(OpenWarp.LOG_PREFIX + "This may result in some data loss! Check the warp configuration for " + this.playerName);
                        return true;
                    }
                }
            }
        }

        // Back
        if (this.plugin.getLocationTracker().getPreviousLocation(this.playerName) != null) {
            Map<String, Object> backWarpConfig = new Warp(this.plugin, TEMP_BACK_NAME, this.plugin.getLocationTracker().getPreviousLocation(this.playerName), this.playerName).getConfigurationMap();
            if (backWarpConfig != null) {
                this.generalConfig.setProperty(OWConfigurationManager.BACK_KEY, backWarpConfig);
            }
        }

        // History stack
        if (this.plugin.getLocationTracker().getLocationStack(this.playerName) != null) {
            Stack<Location> locationStack = this.plugin.getLocationTracker().getLocationStack(this.playerName);
            List<Map<String, Object>> locationStackConfig = new ArrayList<Map<String, Object>>();
            for (Location location : locationStack) {
                locationStackConfig.add(new Warp(this.plugin, TEMP_STACK_NAME, location, this.playerName).getConfigurationMap());
            }
            if (locationStackConfig.size() > 0) {
                this.generalConfig.setProperty(OWConfigurationManager.STACK_KEY, locationStackConfig);
            } else {
                this.generalConfig.setProperty(OWConfigurationManager.STACK_KEY, null);
            }
        }

        // Quotas
        this.quotaConfig.setProperty(OWConfigurationManager.QUOTAS_KEY, this.plugin.getQuotaManager().getPlayerQuotaMap(this.playerName));

        return this.generalConfig.save() && this.warpConfig.save() && this.quotaConfig.save();
    }
}
