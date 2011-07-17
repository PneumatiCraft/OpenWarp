package com.lithium3141.OpenWarp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

public class OWQuotaManager {
    
    private int globalMaxPublicWarps;
    private int globalMaxPrivateWarps;

    private Map<String, Integer> playerMaxPublicWarps = new HashMap<String, Integer>();
    private Map<String, Integer> playerMaxPrivateWarps = new HashMap<String, Integer>();
    
    public static final int QUOTA_UNLIMITED = -1;
    public static final int QUOTA_UNDEFINED = -2;
    
    private OpenWarp plugin;
    
    public OWQuotaManager(OpenWarp plugin) {
        this.plugin = plugin;
    }
    
    public void loadGlobalQuotas(ConfigurationNode configuration) {
        this.globalMaxPublicWarps = configuration.getInt(OpenWarp.QUOTAS_KEY + "." + OpenWarp.QUOTA_PUBLIC_KEY, QUOTA_UNDEFINED);
        this.globalMaxPrivateWarps = configuration.getInt(OpenWarp.QUOTAS_KEY + "." + OpenWarp.QUOTA_PRIVATE_KEY, QUOTA_UNDEFINED);
    }
    
    public void loadPrivateQuotas(String playerName, ConfigurationNode configuration) {
        this.playerMaxPublicWarps.put(playerName, configuration.getInt(OpenWarp.QUOTAS_KEY + "." + OpenWarp.QUOTA_PUBLIC_KEY, QUOTA_UNDEFINED));
        this.playerMaxPrivateWarps.put(playerName, configuration.getInt(OpenWarp.QUOTAS_KEY + "." + OpenWarp.QUOTA_PRIVATE_KEY, QUOTA_UNDEFINED));
    }
    
    public int getPublicWarpQuota(String playerName) {
        int quota = this.globalMaxPublicWarps;
        
        if(this.playerMaxPublicWarps.containsKey(playerName) && this.playerMaxPublicWarps.get(playerName) != QUOTA_UNDEFINED) {
            quota = this.playerMaxPublicWarps.get(playerName);
        }
        
        return (quota == QUOTA_UNDEFINED ? QUOTA_UNLIMITED : quota);
    }
    
    public int getPublicWarpQuota(Player player) {
        return this.getPublicWarpQuota(player.getName());
    }
    
    public int getPrivateWarpQuota(String playerName) {
        int quota = this.globalMaxPrivateWarps;
        
        if(this.playerMaxPrivateWarps.containsKey(playerName) && this.playerMaxPrivateWarps.get(playerName) != QUOTA_UNDEFINED) {
            quota = this.playerMaxPrivateWarps.get(playerName);
        }
        
        return (quota == QUOTA_UNDEFINED ? QUOTA_UNLIMITED : quota);
    }
    
    public int getPrivateWarpQuota(Player player) {
        return this.getPrivateWarpQuota(player.getName());
    }

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
    
    public Map<String, Object> getGlobalQuotaMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        
        result.put(OpenWarp.QUOTA_PUBLIC_KEY, this.globalMaxPublicWarps);
        result.put(OpenWarp.QUOTA_PRIVATE_KEY, this.globalMaxPrivateWarps);
        
        return result;
    }

    public int getGlobalPublicWarpQuota() {
        return this.globalMaxPublicWarps;
    }
    
    public int getGlobalPrivateWarpQuota() {
        return this.globalMaxPrivateWarps;
    }
    
    public Map<String, Integer> getPlayerMaxPublicWarps() {
        return this.playerMaxPublicWarps;
    }

    public Map<String, Integer> getPlayerMaxPrivateWarps() {
        return this.playerMaxPrivateWarps;
    }
    
    public int getPublicWarpCount(String playerName) {
        int count = 0;
        for(Warp warp : this.plugin.getPublicWarps().values()) {
            if(warp.getOwner().equals(playerName)) {
                count++;
            }
        }
        return count;
    }

    public int getPublicWarpCount(Player player) {
        return this.getPublicWarpCount(player.getName());
    }
    
    public int getPrivateWarpCount(String playerName) {
        int count = 0;
        for(Warp warp : this.plugin.getPrivateWarps().get(playerName).values()) {
            if(warp.getOwner().equals(playerName)) {
                count++;
            }
        }
        return count;
    }

    public int getPrivateWarpCount(Player player) {
        return this.getPrivateWarpCount(player.getName());
    }
}
