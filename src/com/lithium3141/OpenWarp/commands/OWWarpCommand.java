package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpCommand extends OWCommand {

	public OWWarpCommand(JavaPlugin plugin) {
		super(plugin);
		
		this.setName("Warp");
        this.setArgRange(1, 1);
        this.setCommandUsage("/warp {NAME}");
        this.addCommandExample("/warp public");
        this.setPermission("openwarp.warp", "Teleport to a warp", PermissionDefault.TRUE);
        this.addKey("warp");
	}

	@Override
	public void runCommand(CommandSender sender, List<String> args) {
	    if(!this.checkPlayerSender(sender)) return;
	    
	    String warpName = args.get(0);
	    
	    Warp target = this.getPlugin().getWarp(sender, warpName);
	    if(target == null) {
	        sender.sendMessage(ChatColor.RED + "No warp found matching name: " + warpName);
	        return;
	    }
	    
	    Player player = (Player)sender;
        if(!player.teleport(target.getLocation())) {
            player.sendMessage(ChatColor.RED + "Error teleporting to warp: " + warpName);
        }
	    
		return;
	}

}
