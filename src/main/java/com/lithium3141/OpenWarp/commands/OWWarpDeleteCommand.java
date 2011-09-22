package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;

/**
 * Deletes a warp owned by the player. Searches the player's public
 * warps first, then private warps.
 */
public class OWWarpDeleteCommand extends OWCommand {

    public OWWarpDeleteCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Warp delete");
        this.setArgRange(1, 1);
        this.setCommandUsage("/warp delete {NAME}");
        this.addCommandExample("/warp delete public");
        this.setPermission("openwarp.warp.delete", "Remove an existing warp", PermissionDefault.OP);
        this.addKey("warp delete");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String warpName = args.get(0);
        String permString = null;
        
        // Remove warp
        if(this.getPlugin().getPublicWarps().containsKey(warpName)) {
            if(this.getPlugin().getPublicWarps().remove(warpName) != null) {
                sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "removed public warp '" + warpName + "'");
                permString = "openwarp.warp.access.public." + warpName;
            } else {
                sender.sendMessage(ChatColor.RED + "No such public warp: " + warpName);
            }
        } else {
            if(!this.checkPlayerSender(sender)) return;
            
            Player player = (Player)sender;
            
            if(this.getPlugin().getPrivateWarps(player.getName()).remove(warpName) != null) {
                sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "removed private warp '" + warpName + "'");
                permString = "openwarp.warp.access.private." + player.getName() + "." + warpName;
            } else {
                sender.sendMessage(ChatColor.RED + "No such warp: " + warpName);
            }
        }
        
        // Remove permission
        if(permString != null) {
            PluginManager pm = this.getPlugin().getServer().getPluginManager();
            pm.removePermission(permString);
            for(Player p : this.getPlugin().getServer().getOnlinePlayers()) {
                p.recalculatePermissions();
            }
        }
        
        this.getPlugin().saveAllConfigurations();
    }

}
