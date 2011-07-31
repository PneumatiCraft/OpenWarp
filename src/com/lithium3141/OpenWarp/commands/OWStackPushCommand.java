package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.Warp;

public class OWStackPushCommand extends OWCommand {

    public OWStackPushCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Stack push");
        this.setArgRange(0, 1);
        this.setCommandUsage("/warp stack push [NAME]");
        this.setCommandExample("/warp stack push public");
        this.setPermission("openwarp.warp.stack.push", "Push a warp onto the location stack", PermissionDefault.TRUE);
        this.addKey("warp stack push");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        Player player = (Player)sender;
        
        if(args.size() == 0) {
            this.getPlugin().getLocationTracker().getLocationStack(player).push(player.getLocation());
        } else {
            String warpName = args.get(0);
            
            Warp target = this.getPlugin().getWarp(player, warpName);
            
            this.getPlugin().getLocationTracker().getLocationStack(player).push(target.getLocation());
            
            if(!player.teleport(target.getLocation())) {
                player.sendMessage(ChatColor.RED + "Error teleporting to warp '" + warpName + "'");
            }
        }
    }

}
