package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;

/**
 * Move to a particular player's current location.
 */
public class OWTeleportCommand extends OWCommand {

    public OWTeleportCommand(JavaPlugin plugin) {
        super(plugin);

        this.setup();
    }

    protected void setup() {
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
        if(!this.senderOK(sender, args)) return;
        Player player = (sender instanceof Player ? (Player)sender : null);

        // Figure out who's going where
        Player sourcePlayer = this.getSourcePlayer(player, args);
        Player targetPlayer = this.getTargetPlayer(player, args);

        if(sourcePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't find player to move");
            return;
        }
        if(targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't find target player");
            return;
        }

        // Move people
        if(!sourcePlayer.teleport(targetPlayer.getLocation())) {
            sourcePlayer.sendMessage(ChatColor.RED + "Error teleporting to player: " + targetPlayer.getName());
        }
    }

    protected boolean senderOK(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player) && args.size() == 1) {
            sender.sendMessage(ChatColor.RED + "You must specify both a source and target player.");
            return false;
        }
        return true;
    }

    protected Player getSourcePlayer(Player sender, List<String> args) {
        if(args.size() == 1) {
            return sender;
        } else {
            return this.getPlugin().getServer().getPlayer(args.get(0));
        }
    }

    protected Player getTargetPlayer(Player sender, List<String> args) {
        return this.getPlugin().getServer().getPlayer(args.get(args.size() - 1));
    }

}
