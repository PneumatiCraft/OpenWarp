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

    /**
     * Create a new instance of the teleport command. Used in command registration.
     *
     * @param plugin The plugin (generally an instance of OpenWarp) backing this command.
     */
    public OWTeleportCommand(JavaPlugin plugin) {
        super(plugin);

        this.setup();
    }

    /**
     * Set up this command instance. Instantiates things like the command name,
     * keys for CommandHandler, examples, usage description, and permissions.
     *
     * Normally, these functions are done by the constructor (and this function
     * is in fact called by the constructor); however, in this case, it is
     * refactored out for extensibility by the OWSummonCommand class.
     */
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
        if (!this.senderOK(sender, args)) return; // SUPPRESS CHECKSTYLE NeedBracesCheck
        Player player = (sender instanceof Player ? (Player)sender : null); // SUPPRESS CHECKSTYLE AvoidInlineConditionalsCheck

        // Figure out who's going where
        Player sourcePlayer = this.getSourcePlayer(player, args);
        Player targetPlayer = this.getTargetPlayer(player, args);

        if (sourcePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't find player to move");
            return;
        }
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't find target player");
            return;
        }

        // Move people
        if (!sourcePlayer.teleport(targetPlayer.getLocation())) {
            sourcePlayer.sendMessage(ChatColor.RED + "Error teleporting to player: " + targetPlayer.getName());
        }
    }

    /**
     * Check whether the sender and argument list together provide enough information
     * to complete the command. Generally, in order to teleport (or summon), the command
     * instance must find both a source and target player; if the command is being issued
     * as a player, then one can be inferred and the args can specify the other. However,
     * on the console (or in a situation with more specificity), both players can be listed
     * in the arguments. This method checks that the command can find both players needed
     * to complete the teleport/summon.
     *
     * @param sender The CommandSender invoking this command.
     * @param args The list of arguments passed in with this command.
     * @return True if both source and target players can be inferred from the sender
     *         and arguments given; false otherwise.
     */
    protected boolean senderOK(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player) && args.size() == 1) {
            sender.sendMessage(ChatColor.RED + "You must specify both a source and target player.");
            return false;
        }
        return true;
    }

    /**
     * Find the "source" Player involved in this command. The source player is the
     * one that will move as a result of the command.
     *
     * @param sender The Player responsible for invoking this command, or null.
     * @param args The arguments passed with this command.
     * @return The Player that will move as a result of this command.
     */
    protected Player getSourcePlayer(Player sender, List<String> args) {
        if (args.size() == 1) {
            return sender;
        } else {
            return this.getPlugin().getServer().getPlayer(args.get(0));
        }
    }

    /**
     * Find the "target" Player involved in this command. The target player is the
     * one that will remain stationary after this command, and will serve as a
     * destination for the source player.
     *
     * @param sender The Player responsible for invoking this command, or null.
     * @param args The arguments passed with this command.
     * @return The Player that will remain stationary through this command.
     */
    protected Player getTargetPlayer(Player sender, List<String> args) {
        return this.getPlugin().getServer().getPlayer(args.get(args.size() - 1));
    }

}
