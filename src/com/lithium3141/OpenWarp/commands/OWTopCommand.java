package com.lithium3141.OpenWarp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWTopCommand extends OWCommand {

    public OWTopCommand(OpenWarp plugin) {
        super(plugin);
        
        this.commandName = "top";
        this.commandDesc = "Get warp information";
        this.commandUsage = "/top";
        this.commandExample = "/top";
        this.commandKeys = new ArrayList<String>() {{ add("top"); }};
        this.minimumArgLength = 0;
        this.maximumArgLength = 0;
        this.opRequired = false;
        this.permission = "top";
    }
    
    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        
        CraftPlayer player = (CraftPlayer)sender;
        Location loc = player.getLocation();
        
        int y = player.getWorld().getHighestBlockYAt(loc);
        loc.setY((double)y);
        
        if(!player.teleport(loc)) {
            player.sendMessage(ChatColor.RED + "Error teleporting to top block!");
        }
        
        return;
    }

}
