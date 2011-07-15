package com.lithium3141.OpenWarp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public abstract class OWCommand extends com.pneumaticraft.commandhandler.Command {
	protected OpenWarp plugin;
	
	public OWCommand(OpenWarp plugin) {
		super(plugin);
	}
	
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
