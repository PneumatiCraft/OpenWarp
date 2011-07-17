package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionException;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpCommand extends OWCommand {

	public OWWarpCommand(OpenWarp plugin) {
		super(plugin);
		
		this.minimumArgs = 1;
		this.maximumArgs = 1;
	}

	@Override
	public boolean execute(CommandSender sender, List<String> args) throws OWPermissionException {
	    if(!this.checkPlayerSender(sender)) return true;
	    
	    String warpName = args.get(0);
	    this.verifyAnyPermission(sender, "openwarp.warp", "openwarp.warp." + warpName);
	    
	    Warp target = this.plugin.getWarp(sender, warpName);
	    if(target == null) {
	        sender.sendMessage(ChatColor.RED + "No warp found matching name: " + warpName);
	        return true;
	    }
	    
	    Player player = (Player)sender;
        if(!player.teleport(target.getLocation())) {
            player.sendMessage(ChatColor.RED + "Error teleporting to warp: " + warpName);
        }
	    
		return true;
	}

}
