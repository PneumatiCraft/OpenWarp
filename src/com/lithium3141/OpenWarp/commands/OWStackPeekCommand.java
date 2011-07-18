package com.lithium3141.OpenWarp.commands;

import java.util.EmptyStackException;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.lithium3141.OpenWarp.OpenWarp;

public class OWStackPeekCommand extends OWStackPopCommand {

    public OWStackPeekCommand(OpenWarp plugin) {
        super(plugin);

        this.minimumArgs = 0;
        this.maximumArgs = 0;
    }
    
    @Override
    protected Location getLocation(Player player) {
        try {
            return this.plugin.getLocationTracker().getLocationStack(player).peek();
        } catch(EmptyStackException e) {
            return null;
        }
    }

}
