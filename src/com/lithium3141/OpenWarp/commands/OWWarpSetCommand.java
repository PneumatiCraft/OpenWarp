package com.lithium3141.OpenWarp.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lithium3141.OpenWarp.OWCommand;
import com.lithium3141.OpenWarp.OpenWarp;

public class OWWarpSetCommand extends OWCommand {

    public OWWarpSetCommand(OpenWarp plugin) {
        super(plugin);
        
        this.minimumArgs = 1;
        this.maximumArgs = 2;
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String commandLabel, List<String> args) {
        // TODO Auto-generated method stub
        return false;
    }

}
