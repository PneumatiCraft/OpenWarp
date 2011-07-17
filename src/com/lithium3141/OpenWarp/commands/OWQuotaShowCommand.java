package com.lithium3141.OpenWarp.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionException;
import com.lithium3141.OpenWarp.OWQuotaManager;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWQuotaShowCommand extends OWCommand {

    public OWQuotaShowCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 0;
        this.maximumArgs = 0;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) throws OWPermissionException {
        this.verifyPermission(sender, "openwarp.warp.quota.show");
        
        OWQuotaManager quotaManager = this.plugin.getQuotaManager();
        
        if(sender instanceof Player) {
            Player player = (Player)sender;
            sender.sendMessage(ChatColor.AQUA + "Quotas: " + ChatColor.WHITE + this.formatQuotaPair(quotaManager.getPublicWarpQuota(player), quotaManager.getPrivateWarpQuota(player)));
        } else {
            sender.sendMessage(ChatColor.GREEN + "Global: " + ChatColor.WHITE + this.formatQuotaPair(quotaManager.getGlobalPublicWarpQuota(), quotaManager.getGlobalPrivateWarpQuota()));
            sender.sendMessage(ChatColor.AQUA + "Players:");
            
            Set<String> quotadPlayers = new HashSet<String>();
            quotadPlayers.addAll(quotaManager.getPlayerMaxPublicWarps().keySet());
            quotadPlayers.addAll(quotaManager.getPlayerMaxPrivateWarps().keySet());
            
            for(String playerName : quotadPlayers) {
                String quotas = this.formatQuotaPair(quotaManager.getPlayerMaxPublicWarps().get(playerName), quotaManager.getPlayerMaxPrivateWarps().get(playerName));
                sender.sendMessage("    " + ChatColor.LIGHT_PURPLE + playerName + ": " + ChatColor.WHITE + quotas);
            }
        }
        
        return true;
    }
    
    private String formatQuotaPair(int publicQuota, int privateQuota) {
        return this.formatQuota(publicQuota, "public") + ", " + this.formatQuota(privateQuota, "private");
    }
    
    private String formatQuota(int quota, String label) {
        switch(quota) {
        case OWQuotaManager.QUOTA_UNDEFINED: return label + " quota undefined";
        case OWQuotaManager.QUOTA_UNLIMITED: return "unlimited " + label;
        default: return quota + " " + label;
        }
    }

}
