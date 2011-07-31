package com.lithium3141.OpenWarp.commands;

import java.util.List;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.Warp;

public class OWStackPrintCommand extends OWCommand {

    public OWStackPrintCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Stack print");
        this.setArgRange(0, 0);
        this.setCommandUsage("/warp stack print");
        this.setCommandExample("/warp stack print");
        this.setPermission("openwarp.warp.stack.print", "Show the location stack", PermissionDefault.TRUE);
        this.addKey("warp stack print");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        Player player = (Player)sender;
        
        Stack<Location> locations = this.getPlugin().getLocationTracker().getLocationStack(player);
        
        for(Location location : locations) {
            sender.sendMessage(this.formatLocation(player, location));
        }
    }
    
    protected String formatLocation(Player player, Location location) {
        Warp matchingWarp = this.getPlugin().getWarp(player, location);
        if(matchingWarp != null) {
            return matchingWarp.getDetailString();
        } else {
            return "(" + location.getX() + "," + location.getY() + "," + location.getZ() + ") in world '" + location.getWorld() + "'";
        }
    }

}
