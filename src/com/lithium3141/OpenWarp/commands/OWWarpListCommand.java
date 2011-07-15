package com.lithium3141.OpenWarp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpListCommand extends OWCommand {

	public OWWarpListCommand(OpenWarp plugin) {
		super(plugin);
		
		this.commandName = "warp list";
        this.commandDesc = "List available warps";
        this.commandUsage = "/warp list [public] [private]";
        this.commandExample = "/warp list public";
        this.commandKeys = new ArrayList<String>() {{ add("warp list"); add("warp"); }};
        this.minimumArgLength = 0;
        this.maximumArgLength = 2;
        this.opRequired = false;
        this.permission = "warp.list";
	}
	
	private String getPublicWarpsList() {
	    Map<String, Warp> publics = this.plugin.getPublicWarps();
        
        String globalsList = ChatColor.GREEN + "Public:" + ChatColor.WHITE;
        if(publics.size() > 0) {
            for(Entry<String, Warp> entry : publics.entrySet()) {
                globalsList += " " + entry.getKey();
            }
        }
        
        return globalsList;
	}
	
	private String getPrivateWarpsList(CommandSender sender) {
	    //TODO implement
	    return "";
	}

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        boolean sendPublic = (args.size() == 0 || args.contains("public"));
        boolean sendPrivate = (args.size() == 0 || args.contains("private"));
        
        if(sendPublic) sender.sendMessage(this.getPublicWarpsList());
        //if(sendPrivate) sender.sendMessage(this.getPrivateWarpsList(sender)); //TODO
        
        return;
    }

}
