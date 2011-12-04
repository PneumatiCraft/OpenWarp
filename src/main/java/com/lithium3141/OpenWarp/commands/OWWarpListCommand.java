package com.lithium3141.OpenWarp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.Warp;

/**
 * List all warps visible to a player. Separates public from private warps.
 */
public class OWWarpListCommand extends OWCommand {

    /**
     * Create a new instance of the warp list command. Used in command registration.
     *
     * @param plugin The plugin (generally an instance of OpenWarp) backing this command.
     */
    public OWWarpListCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Warp set");
        this.setArgRange(0, 3);
        this.setCommandUsage("/warp list [public] [private] [invited]");
        this.addCommandExample("/warp list public");
        this.setPermission("openwarp.warp.list", "Show warps", PermissionDefault.TRUE);
        this.addKey("warp list");
        this.addKey("warp", 0, 0);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        boolean sendPublic = (args.size() == 0 || args.contains("public"));
        boolean sendPrivate = (args.size() == 0 || args.contains("private"));
        boolean sendInvited = (args.size() == 0 || args.contains("invited"));

        if(sendPublic) this.sendPublicWarpsList(sender);
        if(sendPrivate) this.sendPrivateWarpsList(sender);
        if(sendInvited) this.sendInvitedWarpsList(sender);
    }

    /**
     * Send the list of public warps to the given command sender.
     *
     * @param sender The CommandSender that will receive chat messages with the
     *               public warp list.
     */
    private void sendPublicWarpsList(CommandSender sender) {
        Map<String, Warp> publics = this.getPlugin().getPublicWarps();
        sender.sendMessage(ChatColor.GREEN + "Public:" + ChatColor.WHITE + this.formatWarpsList(publics));
    }

    /**
     * Send the list of private warps to the given command sender.
     *
     * @param sender The CommandSender that will receive chat messages with the
     *               private warp list.
     */
    private void sendPrivateWarpsList(CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            Map<String, Warp> privates = this.getPlugin().getPrivateWarps().get(player.getName());
            sender.sendMessage(ChatColor.AQUA + "Private:" + ChatColor.WHITE + this.formatWarpsList(privates));
        } else {
            sender.sendMessage(ChatColor.AQUA + "Private:");

            for(Entry<String, Map<String, Warp>> entry : this.getPlugin().getPrivateWarps().entrySet()) {
                sender.sendMessage("    " + ChatColor.LIGHT_PURPLE + entry.getKey() + ":" + ChatColor.WHITE + this.formatWarpsList(entry.getValue()));
            }
        }
    }

    /**
     * Send the list of invited warps to the given command sender.
     *
     * @param sender The CommandSender that will receive chat messages with the
     *               invited warp list.
     */
    private void sendInvitedWarpsList(CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            List<Warp> invitedWarps = new ArrayList<Warp>();

            for(Entry<String, Map<String, Warp>> mapEntry : this.getPlugin().getPrivateWarps().entrySet()) {
                String targetName = mapEntry.getKey();
                if(targetName.equals(player.getName())) {
                    continue;
                }

                for(Entry<String, Warp> entry : mapEntry.getValue().entrySet()) {
                    Warp warp = entry.getValue();
                    if(warp.isInvited(player)) {
                        System.out.println(player.getName() + " is invited to " + warp.getName() + " by " + warp.getOwner());
                        invitedWarps.add(warp);
                    }
                }
            }

            sender.sendMessage(ChatColor.GOLD + "Invited:" + ChatColor.WHITE + this.formatInvitedWarpsList(invitedWarps));
        }
    }

    /**
     * Format the given set of Warp instances into a human-readable String.
     *
     * @param list A map of warp names to Warp instances that will be listed.
     * @return A color-alternated list of warp names suitable for display to people.
     */
    private String formatWarpsList(Map<String, Warp> list) {
        String result = "";
        if(list.size() > 0) {
            boolean even = false;
            for(String key : list.keySet()) {
                result += " " + (even ? ChatColor.YELLOW : ChatColor.WHITE) + key;
                even = !even;
            }
        }
        return result;
    }

    /**
     * Format the given set of invited Warp instances into a human-readable String.
     * Use this method instead of #formatWarpsList when dealing with only warps
     * shared between players.
     *
     * @param list A list of Warp instances that will be listed.
     * @return A color-alternated list of warp names suitable for display to people.
     */
    private String formatInvitedWarpsList(List<Warp> list) {
        String result = "";
        if(list.size() > 0) {
            boolean even = false;
            for(Warp warp : list) {
                result += " " + (even ? ChatColor.YELLOW : ChatColor.WHITE) + warp.getOwner() + ":" + warp.getName();
                even = !even;
            }
        }
        return result;
    }

}
