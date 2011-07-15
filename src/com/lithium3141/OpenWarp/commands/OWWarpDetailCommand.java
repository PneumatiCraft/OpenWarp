package com.lithium3141.OpenWarp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpDetailCommand extends OWCommand {

    public OWWarpDetailCommand(OpenWarp plugin) {
        super(plugin);
        
        this.commandName = "warp detail";
        this.commandDesc = "Get warp information";
        this.commandUsage = "/warp detail {warp_name}";
        this.commandExample = "/warp detail central";
        this.commandKeys = new ArrayList<String>() {{ add("warp detail"); }};
        this.minimumArgLength = 1;
        this.maximumArgLength = 1;
        this.opRequired = false;
        this.permission = "warp.detail";
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(args.size() == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /warp detail {name}");
            return;
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
        
        return;
    }

}
