package com.lithium3141.OpenWarp.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;
import com.lithium3141.OpenWarp.util.StringUtil;

public class OWWarpListCommand extends OWCommand {

	public OWWarpListCommand(OpenWarp plugin) {
		super(plugin);
		
		this.minimumArgs = 0;
		this.maximumArgs = 2;
	}

	@Override
	public boolean execute(CommandSender sender, Command command, String commandLabel, List<String> args) {
	    boolean sendPublic = (args.size() == 0 || args.contains("public"));
	    boolean sendPrivate = (args.size() == 0 || args.contains("private"));
	    
	    if(sendPublic) this.sendPublicWarpsList(sender);
	    if(sendPrivate) this.sendPrivateWarpsList(sender);
		
		return true;
	}
	
	private void sendPublicWarpsList(CommandSender sender) {
	    Map<String, Warp> publics = this.plugin.getPublicWarps();
        sender.sendMessage(ChatColor.GREEN + "Public:" + ChatColor.WHITE + this.formatWarpsList(publics));
	}
	
	private void sendPrivateWarpsList(CommandSender sender) {
	    if(sender instanceof Player) {
	        Player player = (Player)sender;
	        Map<String, Warp> privates = this.plugin.getPrivateWarps().get(player.getName());
	        sender.sendMessage(ChatColor.AQUA + "Private:" + ChatColor.WHITE + this.formatWarpsList(privates));
	    } else {
	        sender.sendMessage(ChatColor.AQUA + "Private:");
	        
	        for(Entry<String, Map<String, Warp>> entry : this.plugin.getPrivateWarps().entrySet()) {
	            sender.sendMessage("    " + ChatColor.LIGHT_PURPLE + entry.getKey() + ":" + ChatColor.WHITE + this.formatWarpsList(entry.getValue()));
	        }
	    }
	}
	
	private String formatWarpsList(Map<String, Warp> list) {
	    String result = "";
	    if(list.size() > 0) {
            for(Entry<String, Warp> entry : list.entrySet()) {
                result += " " + entry.getKey();
            }
        }
	    return result;
	}

}
