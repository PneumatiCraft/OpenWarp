package com.lithium3141.OpenWarp.commands;

import java.util.EmptyStackException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionException;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWStackPopCommand extends OWCommand {

    public OWStackPopCommand(OpenWarp plugin) {
        super(plugin);

        this.minimumArgs = 0;
        this.maximumArgs = 0;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) throws OWPermissionException {
        if(!this.checkPlayerSender(sender)) return true;
        Player player = (Player)sender;
        
        this.verifyPermission(sender, "openwarp.stack.pop");
        
        Location target = this.getLocation(player);
        if(target != null) {
            if(!player.teleport(target)) {
                player.sendMessage(ChatColor.RED + "Error teleporting you to previous location.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Your warp stack is empty.");
        }

        return true;
    }
    
    protected Location getLocation(Player player) {
        try {
            return this.plugin.getLocationTracker().getLocationStack(player).pop();
        } catch(EmptyStackException e) {
            return null;
        }
    }

}
