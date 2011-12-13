package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.util.BlockSafety;

/**
 * Command to jump to the block under a player's reticle. OpenWarp will
 * match the targeted block as precisely as it can, but may move a player
 * upwards (in the positive Y-direction) until they reach a "safe" location.
 */
public class OWJumpCommand extends OWCommand {

    /**
     * The maximum distance to be checked for a jump target block.
     */
    public static final int MAX_VIEW_DISTANCE = 100;

    /**
     * Create a new instance of the jump command. Used in command registration.
     *
     * @param plugin The plugin (generally an instance of OpenWarp) backing this command.
     */
    public OWJumpCommand(JavaPlugin plugin) {
        super(plugin);

        this.setName("Jump");
        this.setArgRange(0, 0);
        this.setCommandUsage("/jump");
        this.addCommandExample("/jump");
        this.setPermission("openwarp.jump", "Move to position under reticle", PermissionDefault.TRUE);
        this.addKey("jump");
        this.addKey("j");
    }

    /**
     * Error message displayed when failing to find a target block.
     */
    public static final String JUMP_ERROR = "Error finding jump target block; please try again.";

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!this.checkPlayerSender(sender)) return; // SUPPRESS CHECKSTYLE NeedBracesCheck

        Player player = (Player)sender;

        // Get target block and info
        List<Block> blocks = null;
        try {
            blocks = player.getLastTwoTargetBlocks(null, MAX_VIEW_DISTANCE);
        } catch (IllegalStateException e) {
            sender.sendMessage(ChatColor.RED + JUMP_ERROR);
            return;
        }

        if (blocks == null) {
            sender.sendMessage(ChatColor.RED + JUMP_ERROR);
            return;
        }

        Block targetBlock = blocks.get(blocks.size() - 1);
        Location loc = BlockSafety.safeNextUpFrom(targetBlock);
        loc.setPitch(player.getLocation().getPitch());
        loc.setYaw(player.getLocation().getYaw());

        // Transport player
        if (!player.teleport(loc)) {
            player.sendMessage(ChatColor.RED + "Error teleporting to target block!");
        }
    }

}
