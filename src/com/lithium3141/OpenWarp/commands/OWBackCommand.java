package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionException;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWBackCommand extends OWCommand {

    public OWBackCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 0;
        this.maximumArgs = 0;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) throws OWPermissionException {
        if(!this.checkPlayerSender(sender)) return true;
        
        this.verifyPermission(sender, "openwarp.back");
        
        Player player = (Player)sender;
        Location loc = this.plugin.getLocationTracker().getPreviousLocation(player);
        
        if(!player.teleport(loc)) {
            player.sendMessage(ChatColor.RED + "Error returning to previous location!");
        }
        
        return true;
    }

}
