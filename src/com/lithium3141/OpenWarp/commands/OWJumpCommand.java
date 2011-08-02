package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.lithium3141.OpenWarp.OWCommand;

public class OWJumpCommand extends OWCommand {

    public OWJumpCommand(JavaPlugin plugin) {
        super(plugin);
        
        this.setName("Warp set");
        this.setArgRange(0, 0);
        this.setCommandUsage("/jump");
        this.addCommandExample("/jump");
        this.setPermission("openwarp.jump", "Move to position under reticle", PermissionDefault.TRUE);
        this.addKey("jump");
        this.addKey("j");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if(!this.checkPlayerSender(sender)) return;
        
        Player player = (Player)sender;
        
        // Get target block and info
        List<Block> blocks = null;
        try {
            blocks = player.getLastTwoTargetBlocks(null, 100);
        } catch(IllegalStateException e) {
            sender.sendMessage(ChatColor.RED + "Error finding jump target block; please try again.");
        }
        Block targetBlock = blocks.get(blocks.size() - 1);
        Location loc = targetBlock.getLocation();
        
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        System.out.println("Aimed at position (" + x + "," + y + "," + z + ")"); // XXX
        
        // Find the next highest block from requested target that has air above it
        Material last = targetBlock.getType();
        int ny;
        for(ny = y; ny < 127; ny++) {
            Block b = player.getWorld().getBlockAt(x, ny, z);
            Material m = b.getType();
            if(m.equals(Material.AIR) && last.equals(Material.AIR)) {
                break;
            } else {
                last = m;
            }
        }
        
        // Update location for safe jumps
        loc.setY((double)(ny - 1));
        System.out.println("Chose position (" + x + "," + (ny - 1) + "," + z + ")"); // XXX
        loc.setPitch(player.getLocation().getPitch());
        loc.setYaw(player.getLocation().getYaw());
        
        // Transport player
        if(!player.teleport(loc)) {
            player.sendMessage(ChatColor.RED + "Error teleporting to target block!");
        }
    }

}
