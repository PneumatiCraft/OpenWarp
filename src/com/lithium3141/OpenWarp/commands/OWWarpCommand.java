package com.lithium3141.OpenWarp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpCommand extends OWCommand {

	public OWWarpCommand(OpenWarp plugin) {
		super(plugin);
		
		this.commandName = "warp";
        this.commandDesc = "Move to predefined warp position";
        this.commandUsage = "/warp {warp_name}";
        this.commandExample = "/warp central";
        this.commandKeys = new ArrayList<String>() {{ add("warp"); }};
        this.minimumArgLength = 1;
        this.maximumArgLength = 1;
        this.opRequired = false;
        this.permission = "warp";
	}

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        
        // args will have at least one argument (due to trie command mapping & adapter)
        String warpName = args.get(0);
        
        Warp target = this.plugin.getWarp(sender, warpName);
        if(target == null) {
            sender.sendMessage(ChatColor.RED + "No warp found matching name: " + warpName);
            return;
        }
        
        if(!((CraftPlayer)sender).teleport(target.getLocation())) {
            sender.sendMessage(ChatColor.RED + "Error teleporting you to warp: " + warpName);
        }
        
        return;
    }

}
