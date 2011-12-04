package com.lithium3141.OpenWarp.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OWQuotaManager;

/**
 * Show a player's usage of their quotas. Displays both the
 * quotas in effect on a player and their current usage of those
 * quotas (e.g. 2 of 3 public warps).
 */
public class OWQuotaUsageCommand extends OWCommand {

    /**
     * Create a new instance of the quota usage command. Used in command registration.
     *
     * @param plugin The plugin (generally an instance of OpenWarp) backing this command.
     */
    public OWQuotaUsageCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Quota usage");
        this.setArgRange(0, 0);
        this.setCommandUsage("/warp quota [usage]");
        this.addCommandExample("/warp quota");
        this.setPermission("openwarp.warp.quota.usage", "Show warp quota usage", PermissionDefault.TRUE);
        this.addKey("warp quota usage");
        this.addKey("warp quota");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        OWQuotaManager quotaManager = this.getPlugin().getQuotaManager();

        if(sender instanceof Player) {
            Player player = (Player)sender;

            int usedPublic = quotaManager.getPublicWarpCount(player);
            int quotaPublic = quotaManager.getPublicWarpQuota(player);
            int usedPrivate = quotaManager.getPrivateWarpCount(player);
            int quotaPrivate = quotaManager.getPrivateWarpQuota(player);

            String publicString = this.formatUsage(usedPublic, quotaPublic, "public");
            String privateString = this.formatUsage(usedPrivate, quotaPrivate, "private");

            player.sendMessage(ChatColor.AQUA + "Usage: " + ChatColor.WHITE + publicString + ", " + privateString);
        } else {
            Set<String> quotadPlayers = new HashSet<String>();
            quotadPlayers.addAll(quotaManager.getPlayerMaxPublicWarps().keySet());
            quotadPlayers.addAll(quotaManager.getPlayerMaxPrivateWarps().keySet());

            for(String playerName : quotadPlayers) {
                int usedPublic = quotaManager.getPublicWarpCount(playerName);
                int quotaPublic = quotaManager.getPublicWarpQuota(playerName);
                int usedPrivate = quotaManager.getPrivateWarpCount(playerName);
                int quotaPrivate = quotaManager.getPrivateWarpQuota(playerName);

                String publicString = this.formatUsage(usedPublic, quotaPublic, "public");
                String privateString = this.formatUsage(usedPrivate, quotaPrivate, "private");

                sender.sendMessage(ChatColor.LIGHT_PURPLE + playerName + ": " + ChatColor.WHITE + publicString + ", " + privateString);
            }
        }
    }

    /**
     * Get a particular quota usage in a human-readable format. Converts a
     * labeled usage (warps used of total quota) to a String suitable for
     * display on the console or in chat.
     *
     * @param used The number of warps used.
     * @param quota The warp quota being applied.
     * @param label A String describing the quota (e.g. "public").
     * @return A human-readable String describing quota usage.
     */
    private String formatUsage(int used, int quota, String label) {
        if(quota < 0) {
            return "unlimited " + label + " (" + used + " created)";
        } else {
            return used + " / " + quota + " " + label;
        }
    }

}
