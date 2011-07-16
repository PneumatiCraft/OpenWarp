package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
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
        
        // Grab player info
        CraftPlayer player = (CraftPlayer)sender;
        Location playerLoc = player.getLocation();
        
        // TODO quota check
        
        // Find warp type
        String warpType;
        if(args.size() >= 2) {
            warpType = args.get(1);
        } else {
            warpType = "private";
        }
        
        if(!warpType.equals("public") && !warpType.equals("private")) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /warp set {NAME} [public|private]");
        }
        
        // Create and set warp
        Warp warp = new Warp(this.plugin, args.get(0), playerLoc);
        if(warpType.equals("public")) {
            this.plugin.getPublicWarps().put(warp.getName(), warp);
        } else if(warpType.equals("private")) {
            this.plugin.getPrivateWarps().get(player.getName()).put(warp.getName(), warp);
        }
        
        return true;
    }

}
