package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;

public class OWWarpDeleteCommand extends OWCommand {

    public OWWarpDeleteCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Warp delete");
        this.setArgRange(1, 1);
        this.setCommandUsage("/warp delete {NAME}");
        this.setCommandExample("/warp delete public");
        this.setPermission("openwarp.warp.delete", "Remove an existing warp", PermissionDefault.OP);
        this.addKey("warp delete");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String warpName = args.get(0);
        
        if(this.getPlugin().getPublicWarps().containsKey(warpName)) {
            if(this.getPlugin().getPublicWarps().remove(warpName) != null) {
                sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "removed public warp '" + warpName + "'");
            } else {
                sender.sendMessage(ChatColor.RED + "No such public warp: " + warpName);
            }
        } else {
            if(!this.checkPlayerSender(sender)) return;
            
            Player player = (Player)sender;
            
            if(this.getPlugin().getPrivateWarps(player.getName()).remove(warpName) != null) {
                sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "removed private warp '" + warpName + "'");
            } else {
                sender.sendMessage(ChatColor.RED + "No such warp: " + warpName);
            }
        }
        
        this.getPlugin().saveAllConfigurations();
    }

}
