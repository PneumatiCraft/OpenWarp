package com.lithium3141.OpenWarp.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWQuotaManager;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWQuotaUsageCommand extends OWCommand {

    public OWQuotaUsageCommand(OpenWarp plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String commandLabel, List<String> args) {
        OWQuotaManager quotaManager = this.plugin.getQuotaManager();
        
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
        
        return true;
    }
    
    private String formatUsage(int used, int quota, String label) {
        if(quota < 0) {
            return "unlimited " + label + "; " + used + " created";
        } else {
            return used + " / " + quota + " " + label;
        }
    }

}
