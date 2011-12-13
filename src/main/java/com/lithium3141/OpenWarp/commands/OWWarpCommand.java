package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.Warp;

/**
 * Move to a particular warp. This command searches public warps, then
 * the calling player's private warps. If no warp is found matching
 * the requested name, prints a message and does nothing.
 */
public class OWWarpCommand extends OWCommand {

    /**
     * Create a new instance of the warp command. Used in command registration.
     *
     * @param plugin The plugin (generally an instance of OpenWarp) backing this command.
     */
    public OWWarpCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Warp");
        this.setArgRange(1, 1);
        this.setCommandUsage("/warp {NAME}");
        this.addCommandExample("/warp public");
        this.setPermission("openwarp.warp.use", "Teleport to a warp", PermissionDefault.TRUE);
        this.addKey("warp", 1, 1);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!this.checkPlayerSender(sender)) return; // SUPPRESS CHECKSTYLE AvoidInlineConditionalsCheck
        Player player = (Player)sender;

        // Locate the warp
        String warpName = args.get(0);
        Warp target = this.getPlugin().getWarp(sender, warpName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "No warp found matching name: " + warpName);
            return;
        }

        // Verify actual permission to access the warp
        if (target.getOwner().equalsIgnoreCase(player.getName()) || target.isPublic()) {
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
        } else {
            if (!target.isInvited(player)) {
                sender.sendMessage(ChatColor.RED + "You aren't invited to move to warp: " + warpName);
                OpenWarp.DEBUG_LOG.warning("OpenWarp#getWarp() returned warp neither owned or invited. Possible bug.");
                OpenWarp.DEBUG_LOG.warning("    Sender:" + player.getName() + " Warp:" + target.getName() + "Owner:" + target.getOwner());
                return;
            }
        }

        // Move to warp
        if (target.getLocation().getWorld() == null) {
            sender.sendMessage(ChatColor.RED + "Cowardly refusing to move you to a warp without a world");
            return;
        }

        if (!player.teleport(target.getLocation())) {
            player.sendMessage(ChatColor.RED + "Error teleporting to warp: " + warpName);
        }
    }

}
