package com.lithium3141.OpenWarp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpCommand extends OWCommand {

	public OWWarpCommand(OpenWarp plugin) {
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, Command command, String commandLabel, String[] args) {
	    if(!this.checkPlayerSender(sender)) return true;
	    
		// args will have at least one argument (due to trie command mapping & adapter)
	    String warpName = args[0];
	    
	    Warp target = this.plugin.getWarp(sender, warpName);
	    if(target == null) {
	        sender.sendMessage(ChatColor.RED + "No warp found matching name: " + warpName);
	        return true;
	    }
		
	    if(!((CraftPlayer)sender).teleport(target.getLocation())) {
	        sender.sendMessage(ChatColor.RED + "Error teleporting you to warp: " + warpName);
	    }
	    
		return true;
	}

}
