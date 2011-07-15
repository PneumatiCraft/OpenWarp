package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWTopCommand extends OWCommand {

    public OWTopCommand(OpenWarp plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String commandLabel, List<String> args) {
        if(!this.checkPlayerSender(sender)) return true;
        
        CraftPlayer player = (CraftPlayer)sender;
        Location loc = player.getLocation();
        
        int y = player.getWorld().getHighestBlockYAt(loc);
        loc.setY((double)y);
        
        if(!player.teleport(loc)) {
            player.sendMessage(ChatColor.RED + "Error teleporting to top block!");
        }
        
        return true;
    }

}
