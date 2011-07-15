package com.lithium3141.OpenWarp;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public abstract class OWCommand {
	protected OpenWarp plugin;
	
	public OWCommand(OpenWarp plugin) {
		this.plugin = plugin;
	}
	
	public abstract boolean execute(CommandSender sender, Command command, String commandLabel, List<String> args);
	
	public boolean checkPlayerSender(CommandSender sender) {
	    if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Can't move the console!");
            return false;
        } else if(!(sender instanceof CraftPlayer)) {
            sender.sendMessage(ChatColor.RED + "Can't move unknown command sender!");
            return false;
        }
	    return true;
	}
}
