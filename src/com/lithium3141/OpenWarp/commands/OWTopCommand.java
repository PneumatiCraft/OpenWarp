package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;

public class OWTopCommand extends OWCommand {

    public OWTopCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Top");
        this.setArgRange(0, 0);
        this.setCommandUsage("/top");
        this.addCommandExample("/top");
        this.setPermission("openwarp.top", "Move to top block", PermissionDefault.TRUE);
        this.addKey("top");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        
        Player player = (Player)sender;
        Location loc = player.getLocation();
        
        int y = player.getWorld().getHighestBlockYAt(loc);
        loc.setY((double)y);
        
        if(!player.teleport(loc)) {
            player.sendMessage(ChatColor.RED + "Error teleporting to top block!");
        }
    }

}
