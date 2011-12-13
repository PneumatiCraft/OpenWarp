package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWPermissionsHandler;
import com.lithium3141.OpenWarp.Warp;

/**
 * Deletes a warp owned by the player. Searches the player's public
 * warps first, then private warps. Cannot delete shared warps (see
 * the collection of sharing commands for that).
 */
public class OWWarpDeleteCommand extends OWCommand {

    /**
     * Create a new instance of the warp delete command. Used in command registration.
     *
     * @param plugin The plugin (generally an instance of OpenWarp) backing this command.
     */
    public OWWarpDeleteCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Warp delete");
        this.setArgRange(1, 1);
        this.setCommandUsage("/warp delete {NAME}");
        this.addCommandExample("/warp delete public");
        this.setPermission("openwarp.warp.delete.use", "Remove an existing warp", PermissionDefault.OP);
        this.addKey("warp delete");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String warpName = args.get(0);
        String permString = null;

        OWPermissionsHandler permissionsHandler = this.getPlugin().getPermissionsHandler();

        // Remove warp
        if (this.getPlugin().getPublicWarps().containsKey(warpName)) {
            // Get the delete permission in question
            Warp warp = this.getPlugin().getPublicWarps().get(warpName);
            String owner = warp.getOwner();
            String permNode = "other";
            if (sender instanceof Player && ((Player) sender).getName().equals(owner)) {
                permNode = "self";
            }

            if (!permissionsHandler.hasPermission(sender, "openwarp.warp.delete.public." + permNode, false)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to delete that public warp");
                return;
            }

            // Do the delete
            if (this.getPlugin().getPublicWarps().remove(warpName) != null) {
                sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "removed public warp '" + warpName + "'");
                permString = "openwarp.warp.access.public." + warpName;
                this.getPlugin().getConfigurationManager().saveGlobalConfiguration();
            } else {
                sender.sendMessage(ChatColor.RED + "No such public warp: " + warpName);
            }
        } else {
            if (!this.checkPlayerSender(sender)) return; // SUPPRESS CHECKSTYLE AvoidInlineConditionalsCheck

            Player player = (Player) sender;
            String playerName = player.getName();

            if (!permissionsHandler.hasPermission(player, "openwarp.warp.delete.private", false)) {
                player.sendMessage(ChatColor.RED + "You do not have permission to delete that private warp");
                return;
            }

            if (this.getPlugin().getPrivateWarps(playerName).remove(warpName) != null) {
                sender.sendMessage(ChatColor.AQUA + "Success: " + ChatColor.WHITE + "removed private warp '" + warpName + "'");
                permString = "openwarp.warp.access.private." + playerName + "." + warpName;
                this.getPlugin().getConfigurationManager().savePlayerConfiguration(playerName);
            } else {
                sender.sendMessage(ChatColor.RED + "No such warp: " + warpName);
            }

        }

        // Remove permission
        if (permString != null) {
            PluginManager pm = this.getPlugin().getServer().getPluginManager();
            pm.removePermission(permString);
            for (Player p : this.getPlugin().getServer().getOnlinePlayers()) {
                p.recalculatePermissions();
            }
        }
    }

}
