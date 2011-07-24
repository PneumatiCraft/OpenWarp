package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionException;
import com.lithium3141.OpenWarp.OWQuotaManager;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpSetCommand extends OWCommand {

    public OWWarpSetCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 1;
        this.maximumArgs = 2;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) throws OWPermissionException {
        if(!this.checkPlayerSender(sender)) return true;
        
        // Grab player info
        Player player = (Player)sender;
        Location playerLoc = player.getLocation();
        
        // Find warp type
        String warpType;
        if(args.size() >= 2) {
            warpType = args.get(1);
        } else {
            warpType = "private";
        }
        
        if(!warpType.equals("public") && !warpType.equals("private")) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /warp set {NAME} [public|private]");
            return true;
        }
        
        // Check permissions
        this.verifyAnyPermission(sender, "openwarp.warp.set", "openwarp.warp.set." + warpType);
        
        // Check quota
        OWQuotaManager quotaManager = this.plugin.getQuotaManager();
        int quota = Integer.MAX_VALUE;
        if(warpType.equals("public")) {
            if(quotaManager.getPublicWarpQuota(player) >= 0) {
                quota = quotaManager.getPublicWarpQuota(player);
            }
            if(quotaManager.getPublicWarpCount(player) >= quota) {
                player.sendMessage(ChatColor.RED + "You are at your quota (" + quota + ") for public warps.");
                return true;
            }
        } else if(warpType.equals("private")) {
            if(quotaManager.getPrivateWarpQuota(player) >= 0) {
                quota = quotaManager.getPrivateWarpQuota(player);
            }
            if(quotaManager.getPrivateWarpCount(player) >= quota) {
                player.sendMessage(ChatColor.RED + "You are at your quota (" + quota + ") for private warps.");
                return true;
            }
        }
        
        // Create and set warp
        Warp warp = new Warp(this.plugin, args.get(0), playerLoc, player.getName());
        if(warpType.equals("public")) {
            this.plugin.getPublicWarps().put(warp.getName(), warp);
            player.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "Created new public warp '" + warp.getName() + "'");
        } else if(warpType.equals("private")) {
            this.plugin.getPrivateWarps().get(player.getName()).put(warp.getName(), warp);
            player.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "Created new private warp '" + warp.getName() + "'");
        }
        
        this.plugin.saveAllConfigurations();
        
        return true;
    }

}
