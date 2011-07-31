package com.lithium3141.OpenWarp.commands;

import java.util.EmptyStackException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public class OWStackPeekCommand extends OWStackPopCommand {

    public OWStackPeekCommand(JavaPlugin plugin) {
        super(plugin);
    }
    
    @Override
    protected void setup() {
        this.setName("Stack peek");
        this.setArgRange(0, 0);
        this.setCommandUsage("/warp stack peek");
        this.setCommandExample("/warp stack peek");
        this.setPermission("openwarp.warp.stack.peek", "Show the last location on the stack", PermissionDefault.TRUE);
        this.addKey("warp stack peek");
    }
    
    @Override
    protected Location getLocation(Player player) {
        try {
            return this.getPlugin().getLocationTracker().getLocationStack(player).peek();
        } catch(EmptyStackException e) {
            return null;
        }
    }

}
