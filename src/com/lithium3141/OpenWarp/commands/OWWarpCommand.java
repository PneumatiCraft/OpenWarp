package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpCommand extends OWCommand {

	public OWWarpCommand(OpenWarp plugin) {
		super(plugin);
		
		this.minimumArgs = 1;
		this.maximumArgs = 1;
	}

	@Override
	public boolean execute(CommandSender sender, Command command, String commandLabel, List<String> args) {
	    if(!this.checkPlayerSender(sender)) return true;
	    
		// args will have at least one argument (due to trie command mapping)
	    String warpName = args.get(0);
	    
	    Warp target = this.plugin.getWarp(sender, warpName);
	    if(target == null) {
	        sender.sendMessage(ChatColor.RED + "No warp found matching name: " + warpName);
	        return true;
	    }
	    
	    CraftPlayer player = (CraftPlayer)sender;
	    Location prevLoc = player.getLocation();
        if(player.teleport(target.getLocation())) {
            this.plugin.getLocationTracker().setPreviousLocation(player, prevLoc);
        } else {
            player.sendMessage(ChatColor.RED + "Error teleporting to warp: " + warpName);
        }
	    
		return true;
	}

}
