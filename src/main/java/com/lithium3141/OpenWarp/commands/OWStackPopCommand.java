package com.lithium3141.OpenWarp.commands;

import java.util.EmptyStackException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;

/**
 * Move to the location on top of a player's location stack and remove
 * that location from the stack. If the stack is empty, does nothing.
 */
public class OWStackPopCommand extends OWCommand {

    public OWStackPopCommand(JavaPlugin plugin) {
        super(plugin);

        this.setup();
    }

    protected void setup() {
        this.setName("Stack pop");
        this.setArgRange(0, 0);
        this.setCommandUsage("/warp stack pop");
        this.addCommandExample("/warp stack pop");
        this.setPermission("openwarp.warp.stack.pop", "Pop and move to the last location on the stack", PermissionDefault.TRUE);
        this.addKey("warp stack pop");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        Player player = (Player)sender;

        Location target = this.getLocation(player);
        if(target != null) {
            if(!player.teleport(target)) {
                player.sendMessage(ChatColor.RED + "Error teleporting you to previous location.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Your warp stack is empty.");
        }
    }

    protected Location getLocation(Player player) {
        try {
            return this.getPlugin().getLocationTracker().getLocationStack(player).pop();
        } catch(EmptyStackException e) {
            return null;
        }
    }

}
