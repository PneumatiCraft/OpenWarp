package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;

public class OWHomeCommand extends OWCommand {
    
    public OWHomeCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Home");
        this.setArgRange(0, 1);
        this.setCommandUsage("/home [player]");
        this.addCommandExample("/home");
        this.setPermission("openwarp.home.use", "Move to player's home", PermissionDefault.TRUE);
        this.addKey("home");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        Player player = (Player)sender;
        
        Location home = null;
        if(args.size() == 0) {
            home = this.getPlugin().getHomes().get(player.getName());
        } else if(args.size() == 1) {
            // Accessing a specific home - check perms if necessary
            if(!args.get(0).equals(player.getName())) {
                // Somebody else's home
                if(!player.hasPermission("openwarp.home.access." + args.get(0))) {
                    player.sendMessage(ChatColor.RED + "Error: You do not have access to that home.");
                    return;
                }
            }
            
            home = this.getPlugin().getHomes().get(args.get(0));
        }
        
        if(home == null) {
            if(args.size() == 0 || args.get(0).equals(player.getName())) {
                player.sendMessage(ChatColor.RED + "Error: You must first set a home using /home set");
            } else {
                player.sendMessage(ChatColor.RED + "Error: player " + args.get(0) + " has not set a home");
            }
        } else {
            if (!player.teleport(home)) {
                player.sendMessage(ChatColor.RED + "Error teleporting you to your home");
            }
        }
    }

}
