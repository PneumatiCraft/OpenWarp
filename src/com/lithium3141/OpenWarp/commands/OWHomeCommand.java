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
        this.setArgRange(0, 0);
        this.setCommandUsage("/home");
        this.setCommandExample("/home");
        this.setPermission("openwarp.home", "Move to player's home", PermissionDefault.TRUE);
        this.addKey("home");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        Player player = (Player)sender;
        
        Location home = this.getPlugin().getHomes().get(player.getName());
        if(home == null) {
            player.sendMessage(ChatColor.RED + "Error: You must first set a home using /home set");
        } else {
            if(!player.teleport(home)) {
                player.sendMessage(ChatColor.RED + "Error teleporting you to your home");
            }
        }
    }

}
