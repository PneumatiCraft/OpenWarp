package com.lithium3141.OpenWarp.commands;

import java.util.List;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionException;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWStackPrintCommand extends OWCommand {

    public OWStackPrintCommand(OpenWarp plugin) {
        super(plugin);

        this.minimumArgs = 0;
        this.maximumArgs = 0;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) throws OWPermissionException {
        if(!this.checkPlayerSender(sender)) return true;
        Player player = (Player)sender;
        
        this.verifyPermission(sender, "openwarp.stack.print");
        
        Stack<Location> locations = this.plugin.getLocationTracker().getLocationStack(player);
        
        for(Location location : locations) {
            sender.sendMessage(this.formatLocation(player, location));
        }
        
        return true;
    }
    
    protected String formatLocation(Player player, Location location) {
        Warp matchingWarp = this.plugin.getWarp(player, location);
        if(matchingWarp != null) {
            return matchingWarp.getDetailString();
        } else {
            return "(" + location.getX() + "," + location.getY() + "," + location.getZ() + ") in world '" + location.getWorld() + "'";
        }
    }

}
