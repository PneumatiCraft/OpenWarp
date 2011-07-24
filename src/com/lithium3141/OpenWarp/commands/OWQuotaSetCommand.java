package com.lithium3141.OpenWarp.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionException;
import com.lithium3141.OpenWarp.OWQuotaManager;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWQuotaSetCommand extends OWCommand {
    
    protected String usageString;

    public OWQuotaSetCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 2;
        this.maximumArgs = 3;
        
        this.usageString = "Usage: /warp quota set {public|private} {unlimited|VALUE} [PLAYER NAME]";
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) throws OWPermissionException {
        String type = args.get(0);
        if(!type.equals("public") && !type.equals("private")) {
            sender.sendMessage(ChatColor.YELLOW + this.usageString);
            return true;
        }
        
        this.verifyAnyPermission(sender, "openwarp.warp.quota.set", "openwarp.warp.quota.set." + type);
        
        String value = args.get(1);
        int quota = OWQuotaManager.QUOTA_UNDEFINED;
        if(!value.equals("unlimited") && !(Integer.parseInt(value) + "").equals(value)) {
            sender.sendMessage(ChatColor.YELLOW + this.usageString);
            return true;
        } else if(value.equals("unlimited")) {
            quota = OWQuotaManager.QUOTA_UNLIMITED;
        } else {
            quota = Integer.parseInt(value);
        }
        
        String playerName = null;
        if(args.size() > 2) {
            playerName = args.get(2);
        }
        
        OWQuotaManager quotaManager = this.plugin.getQuotaManager();
        
        if(playerName == null) {
            // Setting global quota
            if(type.equals("public")) {
                quotaManager.setGlobalPublicWarpQuota(quota);
            } else if(type.equals("private")) {
                quotaManager.setGlobalPrivateWarpQuota(quota);
            }
            sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "set global " + type + " quota to " + quota);
        } else {
            // Setting a player's quota
            Map<String, Integer> quotaMap = null;
            if(type.equals("public")) {
                quotaMap = quotaManager.getPlayerMaxPublicWarps();
            } else if(type.equals("private")) {
                quotaMap = quotaManager.getPlayerMaxPrivateWarps();
            }
            
            if(quotaMap != null) {
                quotaMap.put(playerName, quota);
                sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "set " + type + " warp quota for " + playerName + " to " + quota);
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown error setting quota! Please report a bug.");
            }
            
        }
        
        this.plugin.saveAllConfigurations();
        
        return true;
    }

}
