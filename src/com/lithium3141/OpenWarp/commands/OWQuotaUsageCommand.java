package com.lithium3141.OpenWarp.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWQuotaManager;

public class OWQuotaUsageCommand extends OWCommand {

    public OWQuotaUsageCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Quota usage");
        this.setArgRange(0, 0);
        this.setCommandUsage("/warp quota [usage]");
        this.addCommandExample("/warp quota");
        this.setPermission("openwarp.warp.quota.usage", "Show warp quota usage", PermissionDefault.TRUE);
        this.addKey("warp quota usage");
        this.addKey("warp quota");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        OWQuotaManager quotaManager = this.getPlugin().getQuotaManager();
        
        if(sender instanceof Player) {
            Player player = (Player)sender;
            
            int usedPublic = quotaManager.getPublicWarpCount(player);
            int quotaPublic = quotaManager.getPublicWarpQuota(player);
            int usedPrivate = quotaManager.getPrivateWarpCount(player);
            int quotaPrivate = quotaManager.getPrivateWarpQuota(player);
            
            String publicString = this.formatUsage(usedPublic, quotaPublic, "public");
            String privateString = this.formatUsage(usedPrivate, quotaPrivate, "private");
            
            player.sendMessage(ChatColor.AQUA + "Usage: " + ChatColor.WHITE + publicString + ", " + privateString);
        } else {
            Set<String> quotadPlayers = new HashSet<String>();
            quotadPlayers.addAll(quotaManager.getPlayerMaxPublicWarps().keySet());
            quotadPlayers.addAll(quotaManager.getPlayerMaxPrivateWarps().keySet());
            
            for(String playerName : quotadPlayers) {
                int usedPublic = quotaManager.getPublicWarpCount(playerName);
                int quotaPublic = quotaManager.getPublicWarpQuota(playerName);
                int usedPrivate = quotaManager.getPrivateWarpCount(playerName);
                int quotaPrivate = quotaManager.getPrivateWarpQuota(playerName);
                
                String publicString = this.formatUsage(usedPublic, quotaPublic, "public");
                String privateString = this.formatUsage(usedPrivate, quotaPrivate, "private");
                
                sender.sendMessage(ChatColor.LIGHT_PURPLE + playerName + ": " + ChatColor.WHITE + publicString + ", " + privateString);
            }
        }
    }
    
    private String formatUsage(int used, int quota, String label) {
        if(quota < 0) {
            return "unlimited " + label + "; " + used + " created";
        } else {
            return used + " / " + quota + " " + label;
        }
    }

}
