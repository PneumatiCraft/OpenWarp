package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.Warp;

/**
 * Push a location onto a player's location stack. If the command is
 * given a warp name, pushes that location; otherwise, pushes the player's
 * current location.
 */
public class OWStackPushCommand extends OWCommand {

    /**
     * Create a new instance of the stack push command. Used in command registration.
     *
     * @param plugin The plugin (generally an instance of OpenWarp) backing this command.
     */
    public OWStackPushCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Stack push");
        this.setArgRange(0, 1);
        this.setCommandUsage("/warp stack push [NAME]");
        this.addCommandExample("/warp stack push public");
        this.setPermission("openwarp.warp.stack.push", "Push a warp onto the location stack", PermissionDefault.TRUE);
        this.addKey("warp stack push");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!this.checkPlayerSender(sender)) return; // SUPPRESS CHECKSTYLE NeedBracesCheck
        Player player = (Player)sender;

        if (args.size() == 0) {
            this.getPlugin().getLocationTracker().getLocationStack(player).push(player.getLocation());
        } else {
            String warpName = args.get(0);

            Warp target = this.getPlugin().getWarp(player, warpName);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "No warp found matching name: " + warpName);
                return;
            }

            String permString = "openwarp.warp.access.*";
            if (target.isPublic()) {
                permString = "openwarp.warp.access.public." + warpName;
            } else {
                permString = "openwarp.warp.access.private." + target.getOwner() + "." + warpName;
            }
            if (!this.getPlugin().getPermissionsHandler().hasPermission(sender, permString, !target.isPublic())) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to move to warp: " + warpName);
                return;
            }

            this.getPlugin().getLocationTracker().getLocationStack(player).push(target.getLocation());

            if (!player.teleport(target.getLocation())) {
                player.sendMessage(ChatColor.RED + "Error teleporting to warp '" + warpName + "'");
            }
        }
    }

}
