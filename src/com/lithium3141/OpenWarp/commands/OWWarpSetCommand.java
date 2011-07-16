package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpSetCommand extends OWCommand {

    public OWWarpSetCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 1;
        this.maximumArgs = 2;
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String commandLabel, List<String> args) {
        if(!this.checkPlayerSender(sender)) return true;
        
        CraftPlayer player = (CraftPlayer)sender;
        Location playerLoc = player.getLocation();
        
        // TODO quota check
        
        // TODO warp type check
        
        Warp warp = new Warp(this.plugin, args.get(0), playerLoc);
        
        this.plugin.getPublicWarps().put(warp.getName(), warp);
        
        return true;
    }

}
