package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionException;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWJumpCommand extends OWCommand {

    public OWJumpCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 0;
        this.maximumArgs = 0;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) throws OWPermissionException {
        if(!this.checkPlayerSender(sender)) return true;
        
        this.verifyPermission(sender, "openwarp.jump");
        
        Player player = (Player)sender;
        
        List<Block> blocks = player.getLastTwoTargetBlocks(null, 100);
        Location loc = blocks.get(blocks.size() - 1).getLocation();
        
        int y = player.getWorld().getHighestBlockYAt(loc);
        loc.setY((double)y);
        loc.setPitch(player.getLocation().getPitch());
        loc.setYaw(player.getLocation().getYaw());
        
        if(!player.teleport(loc)) {
            player.sendMessage(ChatColor.RED + "Error teleporting to target block!");
        }
        
        return true;
    }

}
