package com.lithium3141.OpenWarp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.Warp;

/**
 * List all warps visible to a player. Separates public from private warps.
 */
public class OWWarpListCommand extends OWCommand {

	public OWWarpListCommand(JavaPlugin plugin) {
		super(plugin);
		
		this.setName("Warp set");
        this.setArgRange(0, 3);
        this.setCommandUsage("/warp list [public] [private] [invited]");
        this.addCommandExample("/warp list public");
        this.setPermission("openwarp.warp.list", "Show warps", PermissionDefault.TRUE);
        this.addKey("warp list");
        this.addKey("warp", 0, 0);
	}

	@Override
	public void runCommand(CommandSender sender, List<String> args) {
	    boolean sendPublic = (args.size() == 0 || args.contains("public"));
	    boolean sendPrivate = (args.size() == 0 || args.contains("private"));
        boolean sendInvited = (args.size() == 0 || args.contains("invited"));
	    
	    if(sendPublic) this.sendPublicWarpsList(sender);
	    if(sendPrivate) this.sendPrivateWarpsList(sender);
        if(sendInvited) this.sendInvitedWarpsList(sender);
	}
	
	private void sendPublicWarpsList(CommandSender sender) {
	    Map<String, Warp> publics = this.getPlugin().getPublicWarps();
        sender.sendMessage(ChatColor.GREEN + "Public:" + ChatColor.WHITE + this.formatWarpsList(publics));
	}
	
	private void sendPrivateWarpsList(CommandSender sender) {
	    if(sender instanceof Player) {
	        Player player = (Player)sender;
	        Map<String, Warp> privates = this.getPlugin().getPrivateWarps().get(player.getName());
	        sender.sendMessage(ChatColor.AQUA + "Private:" + ChatColor.WHITE + this.formatWarpsList(privates));
	    } else {
	        sender.sendMessage(ChatColor.AQUA + "Private:");
	        
	        for(Entry<String, Map<String, Warp>> entry : this.getPlugin().getPrivateWarps().entrySet()) {
	            sender.sendMessage("    " + ChatColor.LIGHT_PURPLE + entry.getKey() + ":" + ChatColor.WHITE + this.formatWarpsList(entry.getValue()));
	        }
	    }
	}

    private void sendInvitedWarpsList(CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            List<Warp> invitedWarps = new ArrayList<Warp>();

            for(Entry<String, Map<String, Warp>> mapEntry : this.getPlugin().getPrivateWarps().entrySet()) {
                String targetName = mapEntry.getKey();
                if(targetName.equals(player.getName())) {
                    continue;
                }

                for(Entry<String, Warp> entry : mapEntry.getValue().entrySet()) {
                    Warp warp = entry.getValue();
                    if(warp.isInvited(player)) {
                        System.out.println(player.getName() + " is invited to " + warp.getName() + " by " + warp.getOwner());
                        invitedWarps.add(warp);
                    }
                }
            }

            sender.sendMessage(ChatColor.AQUA + "Invited:" + ChatColor.WHITE + this.formatInvitedWarpsList(invitedWarps));
        }
    }
	
	private String formatWarpsList(Map<String, Warp> list) {
	    String result = "";
	    if(list.size() > 0) {
	        boolean even = false;
            for(String key : list.keySet()) {
                result += " " + (even ? ChatColor.YELLOW : ChatColor.WHITE) + key;
                even = !even;
            }
        }
	    return result;
	}

    private String formatInvitedWarpsList(List<Warp> list) {
        String result = "";
        if(list.size() > 0) {
            boolean even = false;
            for(Warp warp : list) {
                result += " " + (even ? ChatColor.YELLOW : ChatColor.WHITE) + warp.getOwner() + ":" + warp.getName();
                even = !even;
            }
        }
        return result;
    }

}
