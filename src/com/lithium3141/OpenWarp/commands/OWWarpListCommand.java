package com.lithium3141.OpenWarp.commands;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;

public class OWWarpListCommand extends OWCommand {

	public OWWarpListCommand(OpenWarp plugin) {
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, Command command, String commandLabel, String[] args) {
		sender.sendMessage(this.getGlobalWarpsList());
		
		return true;
	}
	
	private String getGlobalWarpsList() {
	    Map<String, Warp> publics = this.plugin.getPublicWarps();
        
        String globalsList = ChatColor.GREEN + "Public:" + ChatColor.WHITE;
        if(publics.size() > 0) {
            for(Entry<String, Warp> entry : publics.entrySet()) {
                globalsList += " " + entry.getKey();
            }
        }
        
        return globalsList;
	}

}
