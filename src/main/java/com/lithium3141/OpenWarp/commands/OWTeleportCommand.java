package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.util.BlockSafety;

/**
 * Move to a particular player's current location.
 */
public class OWTeleportCommand extends OWCommand {

    public OWTeleportCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Teleport");
        this.setArgRange(1, 2);
        this.setCommandUsage("/tp [player] {target player}");
        this.addCommandExample("/tp lithium3141 fernferret");
        this.setPermission("openwarp.teleport", "Teleport to player", PermissionDefault.OP);
        this.addKey("tp");
        this.addKey("tpto");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) {
            if(args.size() == 1) {
                sender.sendMessage(ChatColor.RED + "You must specify both a source and target player.");
                return;
            }
        }

        // Figure out who's going where
        String sourceName;
        String targetName;
        if(args.size() == 1) {
            sourceName = ((Player)sender).getName();
            targetName = args.get(0);
        } else {
            sourceName = args.get(0);
            targetName = args.get(1);
        }

        Player sourcePlayer = this.getPlugin().getServer().getPlayer(sourceName);
        Player targetPlayer = this.getPlugin().getServer().getPlayer(targetName);

        if(sourcePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't determine player to move");
            sender.sendMessage(ChatColor.RED + "This could be a bug! File an issue on GitHub");
            return;
        }
        if(targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't find target player: " + targetName);
            return;
        }
        
        // Move people
        if(!sourcePlayer.teleport(targetPlayer.getLocation())) {
            sourcePlayer.sendMessage(ChatColor.RED + "Error teleporting to player: " + targetPlayer.getName());
        }
    }

}
