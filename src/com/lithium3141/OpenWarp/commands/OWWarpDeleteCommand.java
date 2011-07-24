package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionException;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWWarpDeleteCommand extends OWCommand {

    public OWWarpDeleteCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 1;
        this.maximumArgs = 1;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) throws OWPermissionException {
        String warpName = args.get(0);
        
        this.verifyAnyPermission(sender, "openwarp.warp.delete", "openwarp.warp.delete." + warpName);
        
        if(this.plugin.getPublicWarps().containsKey(warpName)) {
            if(this.plugin.getPublicWarps().remove(warpName) != null) {
                sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "removed public warp '" + warpName + "'");
            } else {
                sender.sendMessage(ChatColor.RED + "No such public warp: " + warpName);
            }
        } else {
            if(!this.checkPlayerSender(sender)) return true;
            
            Player player = (Player)sender;
            
            if(this.plugin.getPrivateWarps(player.getName()).remove(warpName) != null) {
                sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "removed private warp '" + warpName + "'");
            } else {
                sender.sendMessage(ChatColor.RED + "No such warp: " + warpName);
            }
        }
        
        return true;
    }

}
