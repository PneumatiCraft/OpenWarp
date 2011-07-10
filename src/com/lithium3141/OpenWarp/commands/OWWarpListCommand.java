package com.lithium3141.OpenWarp.commands;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;
import com.lithium3141.OpenWarp.util.StringUtil;

public class OWWarpListCommand extends OWCommand {

	public OWWarpListCommand(OpenWarp plugin) {
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, Command command, String commandLabel, String[] args) {
	    boolean sendPublic = (args.length == 0 || StringUtil.arrayContains(args, "public"));
	    boolean sendPrivate = (args.length == 0 || StringUtil.arrayContains(args, "private"));
	    
	    if(sendPublic) sender.sendMessage(this.getPublicWarpsList());
	    //if(sendPrivate) sender.sendMessage(this.getPrivateWarpsList(sender)); //TODO
		
		return true;
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

}
