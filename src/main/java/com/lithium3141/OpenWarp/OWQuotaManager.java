package com.lithium3141.OpenWarp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

/**
 * Utility class to track quota information for players. Stores quotas for both
 * public and private warps for all players known to OpenWarp.
 */
public class OWQuotaManager {
    
    private int globalMaxPublicWarps;
    private int globalMaxPrivateWarps;

    private Map<String, Integer> playerMaxPublicWarps = new HashMap<String, Integer>();
    private Map<String, Integer> playerMaxPrivateWarps = new HashMap<String, Integer>();
    
    /**
     * Special value indicating an unlimited number of warps allowed.
     */
    public static final int QUOTA_UNLIMITED = -1;

    /**
     * Special value indicating an undefined quota.
     */
    public static final int QUOTA_UNDEFINED = -2;
    
    private OpenWarp plugin;
    
    /**
     * Create a new quota manager backed by the given OpenWarp instance.
     *
     * @param plugin The OpenWarp instance to use for various Bukkit queries.
     */
    public OWQuotaManager(OpenWarp plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load global quota information from the given ConfigurationNode. Global quotas
     * affect all players, and act as a sort of baseline for players whose quotas
     * are undefined individually. Players may override global quotas by having their
     * own individual quotas set.
     * 
     * @param configuration The ConfigurationNode instance to read information from.
     */
    public void loadGlobalQuotas(ConfigurationNode configuration) {
        this.globalMaxPublicWarps = configuration.getInt(OpenWarp.QUOTAS_KEY + "." + OpenWarp.QUOTA_PUBLIC_KEY, QUOTA_UNDEFINED);
        this.globalMaxPrivateWarps = configuration.getInt(OpenWarp.QUOTAS_KEY + "." + OpenWarp.QUOTA_PRIVATE_KEY, QUOTA_UNDEFINED);
    }
    
    /**
     * Load individual quotas for players registered with OpenWarp. These quotas, if
     * set to a value (or the special "unlimited" value), will override global quotas.
     *
     * @param playerName The player for whom to load quota information.
     * @param configuration The ConfigurationNode instance to read information from.
     */
    public void loadPrivateQuotas(String playerName, ConfigurationNode configuration) {
        this.playerMaxPublicWarps.put(playerName, configuration.getInt(OpenWarp.QUOTAS_KEY + "." + OpenWarp.QUOTA_PUBLIC_KEY, QUOTA_UNDEFINED));
        this.playerMaxPrivateWarps.put(playerName, configuration.getInt(OpenWarp.QUOTAS_KEY + "." + OpenWarp.QUOTA_PRIVATE_KEY, QUOTA_UNDEFINED));
    }
    
    /**
     * Get the public warp quota for the given player. Calculates the public quota
     * based on both global and individual quotas.
     * <p>
     * If the calculated quota is undefined, it is assumed that the given player has
     * an unlimited quota; this method will never return QUOTA_UNDEFINED.
     *
     * @param playerName The player for whom to calculate a public warp quota.
     * @return The public warp quota for the given player.
     */
    public int getPublicWarpQuota(String playerName) {
        int quota = this.globalMaxPublicWarps;
        
        if(this.playerMaxPublicWarps.containsKey(playerName) && this.playerMaxPublicWarps.get(playerName) != QUOTA_UNDEFINED) {
            quota = this.playerMaxPublicWarps.get(playerName);
        }
        
        return (quota == QUOTA_UNDEFINED ? QUOTA_UNLIMITED : quota);
    }
    
    /**
     * Get the public warp quota for the given Player. 
     *
     * @param player The Player for whom to calculate a public warp quota.
     * @return The public warp quota for the given Player.
     * @see #getPublicWarpQuota(String)
     */
    public int getPublicWarpQuota(Player player) {
        return this.getPublicWarpQuota(player.getName());
    }
    
    /**
     * Get the private warp quota for the given player. Calculates the private quota
     * based on both global and individual quotas.
     * <p>
     * If the calculated quota is undefined, it is assumed that the given player has
     * an unlimited quota; this method will never return QUOTA_UNDEFINED.
     *
     * @param playerName The player for whom to calculate a private warp quota.
     * @return The private warp quota for the given player.
     */
    public int getPrivateWarpQuota(String playerName) {
        int quota = this.globalMaxPrivateWarps;
        
        if(this.playerMaxPrivateWarps.containsKey(playerName) && this.playerMaxPrivateWarps.get(playerName) != QUOTA_UNDEFINED) {
            quota = this.playerMaxPrivateWarps.get(playerName);
        }
        
        return (quota == QUOTA_UNDEFINED ? QUOTA_UNLIMITED : quota);
    }
    
    /**
     * Get the private warp quota for the given Player. 
     *
     * @param player The Player for whom to calculate a private warp quota.
     * @return The private warp quota for the given Player.
     * @see #getPrivateWarpQuota(String)
     */
    public int getPrivateWarpQuota(Player player) {
        return this.getPrivateWarpQuota(player.getName());
    }

    /**
     * Get the raw quota map for the given player. This map is in a format suitable
     * for writing to a Configuration for output to a YAML file; it casts all
     * values into Object instances.
     * <p>
     * This method does not take the global quotas into account; it instead provides
     * the actual per-player value for all quota types. As such, the value set of this
     * map may contain the value QUOTA_UNDEFINED.
     *
     * @param playerName The player for whom to get the quota map.
     * @return A map of strings to values with quota information for the given player.
     */
    public Map<String, Object> getPlayerQuotaMap(String playerName) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        if(this.playerMaxPublicWarps.containsKey(playerName)) {
            result.put(OpenWarp.QUOTA_PUBLIC_KEY, this.playerMaxPublicWarps.get(playerName));
        } else {
            result.put(OpenWarp.QUOTA_PUBLIC_KEY, QUOTA_UNDEFINED);
        }
        
        if(this.playerMaxPrivateWarps.containsKey(playerName)) {
            result.put(OpenWarp.QUOTA_PRIVATE_KEY, this.playerMaxPrivateWarps.get(playerName));
        } else {
            result.put(OpenWarp.QUOTA_PRIVATE_KEY, QUOTA_UNDEFINED);
        }
        
        return result;
    }
    
    /**
     * Get the raw global quota map. This map is in a format suitable for writing
     * to a Configuration for output to a YAML file; it casts all values into
     * Object instances.
     * <p>
     * If a global quota is undefined for a particular quota type, this map may
     * contain the value QUOTA_UNDEFINED.
     *
     * @return A map of strings to values with global quota information.
     */
    public Map<String, Object> getGlobalQuotaMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        
        result.put(OpenWarp.QUOTA_PUBLIC_KEY, this.globalMaxPublicWarps);
        result.put(OpenWarp.QUOTA_PRIVATE_KEY, this.globalMaxPrivateWarps);
        
        return result;
    }

    /**
     * Get the global public warp quota.
     *
     * @return The global public warp quota.
     */
    public int getGlobalPublicWarpQuota() {
        return this.globalMaxPublicWarps;
    }
    
    /**
     * Get the global private warp quota.
     *
     * @return The global private warp quota.
     */
    public int getGlobalPrivateWarpQuota() {
        return this.globalMaxPrivateWarps;
    }
    
    /**
     * Set the global public warp quota.
     *
     * @param quota The new global public warp quota.
     */
    public void setGlobalPublicWarpQuota(int quota) {
        this.globalMaxPublicWarps = quota;
    }
    
    /**
     * Set the global private warp quota.
     *
     * @param quota The new global private warp quota.
     */
    public void setGlobalPrivateWarpQuota(int quota) {
        this.globalMaxPrivateWarps = quota;
    }
    
    /**
     * Get the individual public warp quotas for all players. The returned map
     * will have the actual quota value for each player name known to OpenWarp;
     * as such, the value set of this map may contain the value QUOTA_UNDEFINED.
     *
     * @return A map of player names to public warp quota values.
     */
    public Map<String, Integer> getPlayerMaxPublicWarps() {
        return this.playerMaxPublicWarps;
    }

    /**
     * Get the individual private warp quotas for all players. The returned map
     * will have the actual quota value for each player name known to OpenWarp;
     * as such, the value set of this map may contain the value QUOTA_UNDEFINED.
     *
     * @return A map of player names to private warp quota values.
     */
    public Map<String, Integer> getPlayerMaxPrivateWarps() {
        return this.playerMaxPrivateWarps;
    }
    
    /**
     * Get the number of public warps owned by the given player. The number of
     * public warps is calculated by finding the number of warps in OpenWarp's
     * public warp map with an owner string matching the given player name.
     *
     * @param playerName The player for whom to calculate public warps.
     * @return The number of public warps owned by the given player.
     */
    public int getPublicWarpCount(String playerName) {
        int count = 0;
        for(Warp warp : this.plugin.getPublicWarps().values()) {
            if(warp.getOwner().equals(playerName)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get the number of public warps owned by the given Player.
     *
     * @param player The Player for whom to calculate warps.
     * @return The number of public warps owned by the given Player.
     * @see #getPublicWarpCount(String)
     */
    public int getPublicWarpCount(Player player) {
        return this.getPublicWarpCount(player.getName());
    }
    
    /**
     * Get the number of private warps owned by the given player. The number of
     * private warps is calculated by finding the number of warps in OpenWarp's
     * private warp map with an owner string matching the given player name.
     *
     * @param playerName The player for whom to calculate private warps.
     * @return The number of private warps owned by the given player.
     */
    public int getPrivateWarpCount(String playerName) {
        int count = 0;
        for(Warp warp : this.plugin.getPrivateWarps().get(playerName).values()) {
            if(warp.getOwner().equals(playerName)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get the number of private warps owned by the given Player.
     *
     * @param player The Player for whom to calculate warps.
     * @return The number of private warps owned by the given Player.
     * @see #getPrivateWarpCount(String)
     */
    public int getPrivateWarpCount(Player player) {
        return this.getPrivateWarpCount(player.getName());
    }
}
