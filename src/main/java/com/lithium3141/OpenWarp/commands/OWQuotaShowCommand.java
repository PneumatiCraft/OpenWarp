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
 * Show the quotas in effect on a player.
 */
public class OWQuotaShowCommand extends OWCommand {

    /**
     * Create a new instance of the quota show command. Used in command registration.
     *
     * @param plugin The plugin (generally an instance of OpenWarp) backing this command.
     */
    public OWQuotaShowCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Quota show");
        this.setArgRange(0, 0);
        this.setCommandUsage("/warp quota show");
        this.addCommandExample("/warp quota show");
        this.setPermission("openwarp.warp.quota.show", "Show user warp quota", PermissionDefault.TRUE);
        this.addKey("warp quota show");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        OWQuotaManager quotaManager = this.getPlugin().getQuotaManager();

        if(sender instanceof Player) {
            Player player = (Player)sender;
            sender.sendMessage(ChatColor.AQUA + "Quotas: " + ChatColor.WHITE + this.formatQuotaPair(quotaManager.getPublicWarpQuota(player), quotaManager.getPrivateWarpQuota(player)));
        } else {
            sender.sendMessage(ChatColor.GREEN + "Global: " + ChatColor.WHITE + this.formatQuotaPair(quotaManager.getGlobalPublicWarpQuota(), quotaManager.getGlobalPrivateWarpQuota()));
            sender.sendMessage(ChatColor.AQUA + "Players:");

            Set<String> quotadPlayers = new HashSet<String>();
            quotadPlayers.addAll(quotaManager.getPlayerMaxPublicWarps().keySet());
            quotadPlayers.addAll(quotaManager.getPlayerMaxPrivateWarps().keySet());

            for(String playerName : quotadPlayers) {
                String quotas = this.formatQuotaPair(quotaManager.getPlayerMaxPublicWarps().get(playerName), quotaManager.getPlayerMaxPrivateWarps().get(playerName));
                sender.sendMessage("    " + ChatColor.LIGHT_PURPLE + playerName + ": " + ChatColor.WHITE + quotas);
            }
        }
    }

    /**
     * Format a public/private quota pair for display to a user.
     *
     * @param publicQuota The public warp quota to display.
     * @param privateQuota The private warp quota to display.
     * @return A String showing the public and private quotas in a user-friendly manner.
     */
    private String formatQuotaPair(int publicQuota, int privateQuota) {
        return this.formatQuota(publicQuota, "public") + ", " + this.formatQuota(privateQuota, "private");
    }

    /**
     * Format a particular quota with the given label.
     *
     * @param quota The quota to format.
     * @param label The label to apply to the quota.
     * @return A String showing the given quota in a user-friendly manner.
     */
    private String formatQuota(int quota, String label) {
        switch(quota) {
        case OWQuotaManager.QUOTA_UNDEFINED: return label + " quota undefined";
        case OWQuotaManager.QUOTA_UNLIMITED: return "unlimited " + label;
        default: return quota + " " + label;
        }
    }

}
