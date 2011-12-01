package com.lithium3141.OpenWarp;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.pneumaticraft.commandhandler.PermissionsInterface;

/**
 * Utility class to handle permissions checks for OpenWarp. Implements basic
 * functionality required by CommandHandler for permissions checking and
 * command dispatch. Interfaces with both SuperPerms-based systems and old
 * Permissions plugins.
 */
public class OWPermissionsHandler implements PermissionsInterface {

    private OpenWarp plugin;

    private PermissionHandler permissionHandler;

    /**
     * Create a new permissions handler for the given OpenWarp instance.
     *
     * @param plugin The OpenWarp instance for which to handle permissions.
     */
    public OWPermissionsHandler(OpenWarp plugin) {
        this.plugin = plugin;

        Plugin permissions = this.plugin.getServer().getPluginManager().getPlugin("Permissions");
        if(permissions != null) {
            this.permissionHandler = ((Permissions)permissions).getHandler();
            this.plugin.getServer().getLogger().info(OpenWarp.LOG_PREFIX + "Hooked into Permissions " + permissions.getDescription().getVersion());
        }

    }

    @Override
    public boolean hasPermission(CommandSender sender, String node, boolean isOpRequired) {
        if(!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if(this.permissionHandler != null) {
                return this.permissionHandler.has(player, node);
            } else if(player.hasPermission(node)) {
                return true;
            } else if(isOpRequired) {
                return player.isOp();
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean hasAnyPermission(CommandSender sender, List<String> allPermissionStrings, boolean opRequired) {
        for(String node : allPermissionStrings) {
            if(this.hasPermission(sender, node, opRequired)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAllPermission(CommandSender sender, List<String> allPermissionStrings, boolean opRequired) {
        for(String node : allPermissionStrings) {
            if(!this.hasPermission(sender, node, opRequired)) {
                return false;
            }
        }
        return true;
    }

}
