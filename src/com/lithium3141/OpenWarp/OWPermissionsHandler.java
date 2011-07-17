package com.lithium3141.OpenWarp;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class OWPermissionsHandler {
    
    private OpenWarp plugin;
    
    private PermissionHandler permissionHandler;
    
    public OWPermissionsHandler(OpenWarp plugin) {
        this.plugin = plugin;
        
        Plugin permissions = this.plugin.getServer().getPluginManager().getPlugin("Permissions");
        if(permissions != null) {
            this.permissionHandler = ((Permissions)permissions).getHandler();   
            this.plugin.getServer().getLogger().info(OpenWarp.LOG_PREFIX + "Hooked into Permissions " + permissions.getDescription().getVersion());
        }
        
    }
    
    public boolean hasPermission(CommandSender sender, String permission) {
        if(!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if(this.permissionHandler != null) {
                return this.permissionHandler.has(player, permission);
            } else {
                return player.hasPermission(permission);
            }
        }
    }
    
}
