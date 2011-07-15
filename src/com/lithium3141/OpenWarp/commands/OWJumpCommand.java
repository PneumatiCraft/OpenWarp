package com.lithium3141.OpenWarp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWJumpCommand extends OWCommand {

    public OWJumpCommand(OpenWarp plugin) {
        super(plugin);
        
        this.commandName = "jump";
        this.commandDesc = "Move to reticle target";
        this.commandUsage = "/jump";
        this.commandExample = "/jump";
        this.commandKeys = new ArrayList<String>() {{ add("jump"); add("j"); }};
        this.minimumArgLength = 0;
        this.maximumArgLength = 0;
        this.opRequired = false;
        this.permission = "jump";
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        
        CraftPlayer player = (CraftPlayer)sender;
        
        List<Block> blocks = player.getLastTwoTargetBlocks(null, 100);
        Location loc = blocks.get(blocks.size() - 1).getLocation();
        
        int y = player.getWorld().getHighestBlockYAt(loc);
        loc.setY((double)y);
        loc.setPitch(player.getLocation().getPitch());
        loc.setYaw(player.getLocation().getYaw());
        
        if(!player.teleport(loc)) {
            player.sendMessage(ChatColor.RED + "Error teleporting to target block!");
        }
    }

}
