package com.lithium3141.OpenWarp.commands;

import java.util.List;
import java.util.Stack;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.Warp;

/**
 * Print the locations on a player's location stack. Locations are printed
 * in reverse order; that is, the location on the top of the stack will
 * be the last printed.
 */
public class OWStackPrintCommand extends OWCommand {

    /**
     * Construct a new instance of the stack print command. Used in command registration.
     */
    public OWStackPrintCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Stack print");
        this.setArgRange(0, 0);
        this.setCommandUsage("/warp stack print");
        this.addCommandExample("/warp stack print");
        this.setPermission("openwarp.warp.stack.print", "Show the location stack", PermissionDefault.TRUE);
        this.addKey("warp stack print");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        Player player = (Player)sender;

        Stack<Location> locations = this.getPlugin().getLocationTracker().getLocationStack(player);

        if(locations.size() == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Your warp stack is currently empty.");
            return;
        }

        for(Location location : locations) {
            sender.sendMessage(this.formatLocation(player, location));
        }
    }

    /**
     * Format a location into a human-readable String. Converts Location instances
     * into Warp objects where possible, using the name of the warp to simplify
     * the location display; otherwise, provides exact coordinates within a World.
     *
     * @param player The Player whose Warp instances are checked for Location matches
     *               (generally, the sender of this command).
     * @param location The Location instance to format.
     * @return A String representing the given Location that can be displayed nicely
     *         to the given Player.
     */
    protected String formatLocation(Player player, Location location) {
        Warp matchingWarp = this.getPlugin().getWarp(player, location);
        if(matchingWarp != null) {
            return matchingWarp.getDetailString();
        } else {
            return "(" + location.getX() + "," + location.getY() + "," + location.getZ() + ") in world '" + location.getWorld() + "'";
        }
    }

}
