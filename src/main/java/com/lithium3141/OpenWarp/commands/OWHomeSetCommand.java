package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;

public class OWHomeSetCommand extends OWCommand {

    public OWHomeSetCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Set home");
        this.setArgRange(0, 0);
        this.setCommandUsage("/home set");
        this.addCommandExample("/home set");
        this.setPermission("openwarp.home.set", "Set a new home", PermissionDefault.TRUE);
        this.addKey("home set");
        this.addKey("sethome");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        Player player = (Player)sender;
        
        this.getPlugin().getHomes().put(player.getName(), player.getLocation());
        this.getPlugin().saveConfigurations(player);
        
        player.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "Set your home to your current location.");
        
        String permString = "openwarp.home.access." + player.getName();
        
        PluginManager pm = this.getPlugin().getServer().getPluginManager();
        if(pm.getPermission(permString) == null) {
            Permission homeAccessPerm = new Permission(permString, PermissionDefault.OP);
            pm.addPermission(homeAccessPerm);
            
            Permission allHomePerm = pm.getPermission("openwarp.home.access.*");
            allHomePerm.getChildren().put(permString, true);
            allHomePerm.recalculatePermissibles();
        }
    }

}
