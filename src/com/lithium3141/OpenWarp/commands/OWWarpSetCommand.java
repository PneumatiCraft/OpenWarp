package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWQuotaManager;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpSetCommand extends OWCommand {

    public OWWarpSetCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Warp set");
        this.setArgRange(1, 2);
        this.setCommandUsage("/warp set {NAME} [public|private]");
        this.addCommandExample("/warp set community public");
        this.setPermission("openwarp.warp.set", "Create a new warp", PermissionDefault.OP);
        this.addKey("warp set");
        this.addKey("setwarp");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        
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
            player.sendMessage(ChatColor.YELLOW + this.getCommandUsage());
            return;
        }
        
        // Check quota
        OWQuotaManager quotaManager = this.getPlugin().getQuotaManager();
        int quota = Integer.MAX_VALUE;
        if(warpType.equals("public")) {
            if(quotaManager.getPublicWarpQuota(player) >= 0) {
                quota = quotaManager.getPublicWarpQuota(player);
            }
            if(quotaManager.getPublicWarpCount(player) >= quota) {
                player.sendMessage(ChatColor.RED + "You are at your quota (" + quota + ") for public warps.");
                return;
            }
        } else if(warpType.equals("private")) {
            if(quotaManager.getPrivateWarpQuota(player) >= 0) {
                quota = quotaManager.getPrivateWarpQuota(player);
            }
            if(quotaManager.getPrivateWarpCount(player) >= quota) {
                player.sendMessage(ChatColor.RED + "You are at your quota (" + quota + ") for private warps.");
                return;
            }
        }
        
        // Create and set warp
        Warp warp = new Warp(this.getPlugin(), args.get(0), playerLoc, player.getName());
        if(warpType.equals("public")) {
            this.getPlugin().getPublicWarps().put(warp.getName(), warp);
            player.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "Created new public warp '" + warp.getName() + "'");
        } else if(warpType.equals("private")) {
            this.getPlugin().getPrivateWarps().get(player.getName()).put(warp.getName(), warp);
            player.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "Created new private warp '" + warp.getName() + "'");
        }
        
        // Create permission for warp
        String permString = "";
        if(warpType.equals("public")) {
            permString = "openwarp.warp.access.public." + warp.getName();
        } else if(warpType.equals("private")) {
            permString = "openwarp.warp.access.private." + warp.getOwner() + "." + warp.getName();
        }
        Permission accessPerm = new Permission(permString, PermissionDefault.TRUE);
        PluginManager pm = this.getPlugin().getServer().getPluginManager();
        if(pm.getPermission(permString) == null) {
            pm.addPermission(accessPerm);
            Permission parentPerm = pm.getPermission("openwarp.warp.access." + warpType + ".*");
            parentPerm.getChildren().put(permString, true);
            accessPerm.recalculatePermissibles();
            parentPerm.recalculatePermissibles();
            for(Player p : this.getPlugin().getServer().getOnlinePlayers()) {
                p.recalculatePermissions();
            }
        }
        
        this.getPlugin().saveAllConfigurations();
    }

}
