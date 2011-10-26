package com.lithium3141.OpenWarp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.pneumaticraft.commandhandler.Command;

/**
 * Abstract parent class for all executable commands in OpenWarp. Every command
 * handled by OpenWarp is a concrete subclass of OWCommand, which itself subclasses
 * CommandHandler's Command class.
 */
public abstract class OWCommand extends Command {

    /**
     * The command namespace to use when configured. This namespace is
     * prefixed to every command key path.
     */
    public static final String NAMESPACE_PREFIX = "ow";
    
    /**
     * Instantiate a command backed by the given plugin. The plugin is used
     * in subclasses for various queries back into Bukkit.
     *
     * @param plugin The plugin used for Bukkit calls in concrete command
     * subclasses.
     */
	public OWCommand(JavaPlugin plugin) {
        super(plugin);
    }
	
    /**
     * Get this OWCommand's plugin. Casts the plugin passed in the constructor
     * to an instance of OpenWarp.
     *
     * @see #OWCommand(JavaPlugin)
     *
     * @return The OpenWarp plugin instance handling this OWCommand.
     */
	public OpenWarp getPlugin() {
	    return (OpenWarp)this.plugin;
	}
	
    /**
     * Check if the sender of this OWCommand is a Player. If the sender is not a
     * Player, send an error message back.
     *
     * @param sender The CommandSender to check.
     * @return true if the sender is a Player; false otherwise.
     */
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

    @Override
    public void addKey(String key) {
        if(this.getPlugin().getConfigurationManager().usingCommandNamespace()) {
            super.addKey(NAMESPACE_PREFIX + key);
        } else {
            super.addKey(key);
        }
    }

    @Override
    public void addKey(String key, int minArgs, int maxArgs) {
        if(this.getPlugin().getConfigurationManager().usingCommandNamespace()) {
            super.addKey(NAMESPACE_PREFIX + key, minArgs, maxArgs);
        } else {
            super.addKey(key, minArgs, maxArgs);
        }
    }
}
