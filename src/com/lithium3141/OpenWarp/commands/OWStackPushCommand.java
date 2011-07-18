package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionException;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWStackPushCommand extends OWCommand {

    public OWStackPushCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 0;
        this.maximumArgs = 1;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) throws OWPermissionException {
        if(!this.checkPlayerSender(sender)) return true;
        Player player = (Player)sender;
        
        this.verifyPermission(sender, "openwarp.stack.push");
        
        if(args.size() == 0) {
            this.plugin.getLocationTracker().getLocationStack(player).push(player.getLocation());
        } else {
            String warpName = args.get(0);
            
            this.verifyAnyPermission(sender, "openwarp.warp", "openwarp.warp." + warpName);
            
            Warp target = this.plugin.getWarp(player, warpName);
            
            this.plugin.getLocationTracker().getLocationStack(player).push(target.getLocation());
            
            if(!player.teleport(target.getLocation())) {
                player.sendMessage(ChatColor.RED + "Error teleporting to warp '" + warpName + "'");
            }
        }
        
        return true;
    }

}
