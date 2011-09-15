package com.lithium3141.OpenWarp.util;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.lithium3141.OpenWarp.OpenWarp;
import com.lithium3141.OpenWarp.Warp;
import com.onarandombox.utils.MVDestination;

public class OpenWarpDestination implements MVDestination {

    private OpenWarp plugin;
    private Warp warp;
    private String warpName;

    @Override
    public String getIdentifer() {
        return "ow";
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String dest) {
        if (dest.split(":").length == 2) {
            return true;
        }
        return false;
    }

    @Override
    public Location getLocation(Entity e) {
        if (e instanceof CommandSender) {
            Warp w = this.plugin.getWarp((CommandSender)e, this.warpName);
            if(w != null) {
                return w.getLocation();
            }
        }
        return null;
    }

    @Override
    public boolean isValid() {
        return this.plugin.getWarp(null, this.warpName) != null;
    }

    @Override
    public void setDestination(JavaPlugin plugin, String dest) {
        // If this class exists, then this plugin MUST exist!
        this.plugin = (OpenWarp) plugin.getServer().getPluginManager().getPlugin("OpenWarp");
        try {
            this.warpName = dest.split(":")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            this.warpName = "";
        }
    }

    @Override
    public String getType() {
        Warp w = this.plugin.getWarp(null, this.warpName);
        if (w != null) {
            if (w.isPublic()) {
                return "Public OpenWarp";
            }
            return "Private OpenWarp";
        }
        return "Invalid OpenWarp";
    }

    @Override
    public String getName() {
        Warp w = this.plugin.getWarp(null, this.warpName);
        if (w != null) {
            return w.getName();
        }
        return "Invalid OpenWarp";
    }

    @Override
    public String getRequiredPermission() {
        Warp w = this.plugin.getWarp(null, this.warpName);
        String permString = "";
        if (w != null) {
            if (w.isPublic()) {
                permString = "openwarp.warp.access.public." + w.getName();
            } else {
                permString = "openwarp.warp.access.private." + w.getOwner() + "." + w.getName();
            }
        }
        return permString;
    }

    @Override
    public Vector getVelocity() {
        return new Vector();
    }

    public boolean useSafeTeleporter() {
        Warp w = this.plugin.getWarp(null, this.warpName);
        if (w != null)
            return warp.useSafeTeleporter();
        return true;
    }

}
