package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;

public class OWBackCommand extends OWCommand {

    public OWBackCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Back");
        this.setArgRange(0, 0);
        this.setCommandUsage("/back");
        this.addCommandExample("/back");
        this.setPermission("openwarp.back", "Move to previous location", PermissionDefault.TRUE);
        this.addKey("back");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        
        Player player = (Player)sender;
        Location loc = this.getPlugin().getLocationTracker().getPreviousLocation(player);
        
        if(loc == null) {
            player.sendMessage(ChatColor.RED + "You do not currently have a previous location!");
            return;
        }
        
        if(!player.teleport(loc)) {
            player.sendMessage(ChatColor.RED + "Error returning to previous location!");
        }
    }

}
