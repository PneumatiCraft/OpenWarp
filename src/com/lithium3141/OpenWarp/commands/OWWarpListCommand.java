package com.lithium3141.OpenWarp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWWarpListCommand extends OWCommand {

	public OWWarpListCommand(OpenWarp plugin) {
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, Command command, String commandLabel, String[] args) {
		sender.sendMessage("DEBUG");
		
		// TODO Auto-generated method stub
		return true;
	}

}
