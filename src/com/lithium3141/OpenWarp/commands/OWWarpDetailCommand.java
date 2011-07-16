package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpDetailCommand extends OWCommand {

    public OWWarpDetailCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 1;
        this.maximumArgs = 1;
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String commandLabel, List<String> args) {
        if(args.size() == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /warp detail {name}");
            return true;
        }
        
        for(String warpName : args) {
            Warp warp = this.plugin.getWarp(sender, warpName);
            if(warp == null) {
                sender.sendMessage(ChatColor.RED + warpName + ":" + ChatColor.WHITE + " No such warp");
            } else {
                ChatColor color = (warp.isPublic() ? ChatColor.GREEN : ChatColor.BLUE);
                sender.sendMessage(color + warpName + ":" + ChatColor.WHITE + " " + warp.getDetailString());
            }
        }
        
        return true;
    }

}
