package com.lithium3141.OpenWarp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class OWCommand {
	protected OpenWarp plugin;
	
	public OWCommand(OpenWarp plugin) {
		this.plugin = plugin;
	}
	
	public abstract boolean execute(CommandSender sender, Command command, String commandLabel, String[] args); 
}
