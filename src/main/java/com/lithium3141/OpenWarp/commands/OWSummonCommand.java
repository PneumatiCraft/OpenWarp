package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Summon a player to your current location
 */
public class OWSummonCommand extends OWTeleportCommand {

    /**
     * Create a new instance of the summon command. Used in command registration.
     *
     * @param plugin The plugin (generally an instance of OpenWarp) backing this command.
     */
    public OWSummonCommand(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void setup() {
        this.setName("Summon");
        this.setArgRange(1, 1);
        this.setCommandUsage("/summon {player}");
        this.addCommandExample("/summon fernferret");
        this.setPermission("openwarp.summon", "Summon player", PermissionDefault.OP);
        this.addKey("summon");
    }

    @Override
    protected boolean senderOK(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Command must be sent from in-game!");
            return false;
        }
        return true;
    }

    @Override
    protected Player getSourcePlayer(Player sender, List<String> args) {
        return this.getPlugin().getServer().getPlayer(args.get(0));
    }

    @Override
    protected Player getTargetPlayer(Player sender, List<String> args) {
        return sender;
    }

}
