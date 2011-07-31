package com.lithium3141.OpenWarp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.pneumaticraft.commandhandler.Command;

public abstract class OWCommand extends Command {
    
	public OWCommand(JavaPlugin plugin) {
        super(plugin);
    }
	
	public OpenWarp getPlugin() {
	    return (OpenWarp)this.plugin;
	}
	
	public boolean checkPlayerSender(CommandSender sender) {
	    if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Command must be run in-game!");
            return false;
        } else if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cannot verify command's sender!");
            return false;
        }
	    return true;
	}
}
