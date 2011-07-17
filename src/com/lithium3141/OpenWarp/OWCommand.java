package com.lithium3141.OpenWarp;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class OWCommand {
	protected OpenWarp plugin;
	
	protected int minimumArgs;
	protected int maximumArgs;
	
	public OWCommand(OpenWarp plugin) {
		this.plugin = plugin;
	}
	
	public abstract boolean execute(CommandSender sender, Command command, String commandLabel, List<String> args);
	
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
	
	public int getMinimumArgs() {
        return this.minimumArgs;
    }

    public void setMinimumArgs(int minimumArgs) {
        this.minimumArgs = minimumArgs;
    }

    public int getMaximumArgs() {
        return this.maximumArgs;
    }

    public void setMaximumArgs(int maximumArgs) {
        this.maximumArgs = maximumArgs;
    }
}
