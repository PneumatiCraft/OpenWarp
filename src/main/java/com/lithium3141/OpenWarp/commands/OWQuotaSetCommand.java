package com.lithium3141.OpenWarp.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWQuotaManager;

/**
 * Set the quota for a certain type of warp, either globally or on
 * a particular player. Warp quotas are managed using the OWQuotaManager
 * class.
 */
public class OWQuotaSetCommand extends OWCommand {

    public OWQuotaSetCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Quota set");
        this.setArgRange(2, 3);
        this.setCommandUsage("/warp quota set {public|private} {unlimited|VALUE} [PLAYER NAME]");
        this.addCommandExample("/warp quota set public 3 Bob");
        this.setPermission("openwarp.warp.quota.set", "Create a new warp", PermissionDefault.OP);
        this.addKey("warp quota set");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String type = args.get(0);
        if(!type.equals("public") && !type.equals("private")) {
            sender.sendMessage(ChatColor.YELLOW + this.getCommandUsage());
            return;
        }

        String value = args.get(1);
        int quota = OWQuotaManager.QUOTA_UNDEFINED;
        if(!value.equals("unlimited") && !(Integer.parseInt(value) + "").equals(value)) {
            sender.sendMessage(ChatColor.YELLOW + this.getCommandUsage());
            return;
        } else if(value.equals("unlimited")) {
            quota = OWQuotaManager.QUOTA_UNLIMITED;
        } else {
            quota = Integer.parseInt(value);
        }

        String playerName = null;
        if(args.size() > 2) {
            playerName = args.get(2);
        }

        OWQuotaManager quotaManager = this.getPlugin().getQuotaManager();

        if(playerName == null) {
            // Setting global quota
            if(type.equals("public")) {
                quotaManager.setGlobalPublicWarpQuota(quota);
            } else if(type.equals("private")) {
                quotaManager.setGlobalPrivateWarpQuota(quota);
            }
            sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "set global " + type + " quota to " + quota);
            this.getPlugin().getConfigurationManager().saveGlobalConfiguration();
        } else {
            // Setting a player's quota
            Map<String, Integer> quotaMap = null;
            if(type.equals("public")) {
                quotaMap = quotaManager.getPlayerMaxPublicWarps();
            } else if(type.equals("private")) {
                quotaMap = quotaManager.getPlayerMaxPrivateWarps();
            }

            OpenWarp.DEBUG_LOG.fine("Setting warp quota for " + playerName + " to " + quota);
            if(quotaMap != null) {
                quotaMap.put(playerName, quota);
                sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "set " + type + " warp quota for " + playerName + " to " + quota);
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown error setting quota! Please report a bug.");
            }

            this.getPlugin().getConfigurationManager().savePlayerConfiguration(playerName);
        }
    }

}
