package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.Warp;

/**
 * Share a private warp with another player.
 */
public class OWWarpShareCommand extends OWCommand {

    /**
     * Create a new instance of the warp share command. Used in command registration.
     *
     * @param plugin The plugin (generally an instance of OpenWarp) backing this command.
     */
    public OWWarpShareCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Warp share");
        this.setArgRange(2, 2);
        this.setCommandUsage("/warp share {warp} {player}");
        this.addCommandExample("/warp share MyWarp OtherPerson");
        this.setPermission("openwarp.warp.share", "Share a private warp with other users", PermissionDefault.TRUE);
        this.addKey("warp share");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        Player player = (Player)sender;

        String warpName = args.get(0);
        String sharePlayerName = args.get(1);

        Warp warp = this.getPlugin().getPrivateWarps().get(player.getName()).get(warpName);
        if(warp == null) {
            sender.sendMessage(ChatColor.RED + "Could not find private warp '" + warpName + "' - not sharing.");
            return;
        }

        warp.addInvitee(sharePlayerName);

        Player sharePlayer = this.getPlugin().getServer().getPlayer(sharePlayerName);
        if(sharePlayer != null) {
            sharePlayer.sendMessage(ChatColor.GOLD + player.getName() + " has shared warp '" + warpName + "' with you!");
            sharePlayer.sendMessage(ChatColor.GOLD + "Access via: /warp " + player.getName() + ":" + warpName);
        }
    }

}
